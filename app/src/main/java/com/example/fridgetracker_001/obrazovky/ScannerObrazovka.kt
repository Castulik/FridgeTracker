package com.example.fridgetracker_001.obrazovky

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.viewmodel.CodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun ScannerObrazovkaRoute(
    navController: NavController,
    returnRoute: String,
    codeViewModel: CodeViewModel,
    skladId: Int
) {
    val coroutineScope = rememberCoroutineScope()
    // V tomto composable už jen zavoláme "ScannerPovoleni"
    // a výsledek odešleme zpět:
    ScannerPovoleni(
        onScanned = { code ->

            if (returnRoute == "") {
                coroutineScope.launch {

                    val codeEntity = codeViewModel.getCodeEntityByBarcode(code)
                    if (codeEntity != null) {
                        navController.navigate("pridatPotravinu/$skladId?barcode=$code")
                    } else
                        navController.navigate("KategorieObrazovka/$skladId/sklad?barcode=$code")
                }
            } else {
                val finalRoute = returnRoute + Uri.encode(code)
                navController.navigate(finalRoute)
            }
        },
        onCancel = {
            navController.popBackStack()
        }
    )
}

@Composable
fun ScannerPovoleni(
    onScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // Zjistíme, zda už permission máme:
    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher pro žádost o permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            // Dostali jsme povolení
            cameraGranted = true
        } else {
            // Uživatel odmítl - rovnou zrušíme skenování
            onCancel()
        }
    }

    // Po prvním zobrazení composable (nebo kdykoliv se cameraGranted změní),
    // pokud permission nemáme, hned spustíme žádost
    LaunchedEffect(cameraGranted) {
        if (!cameraGranted) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Pokud máme povolení => zobrazí se ScannerContent
    if (cameraGranted) {
        ScannerContent(
            onScanned = onScanned,
            onCancel = onCancel
        )
    } else {
        // Zatím nic (ani text) – jen čekáme na výsledek z permission dialogu
        // Pokud uživatel odmítne, onCancel() se zavolá.
    }
}


// Třeba reálný scanner z minulé ukázky
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerContent(
    onScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    // Díky Box(modifier = Modifier.fillMaxSize()) pokrývá celé okno aplikace (full-screen).
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Vnitřní obsah: náš CameraPreview + "horní" lišta s tlačítkem Zrušit
        Column(modifier = Modifier.fillMaxSize()) {
            // Horní AppBar (můžeš upravit layout podle sebe)
            TopAppBar(
                title = { Text(stringResource(R.string.ostatni_scan), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět", tint = Color.White)
                    }
                },
                actions = {
                },
                colors = MaterialTheme.colorScheme.let {
                    // Např. tmavý bar
                    androidx.compose.material3.TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF202020)
                    )
                }
            )

            // Náhled kamery + zpracování obrazu
            CameraPreviewWithAnalyzer(onScanned = { detectedValue ->
                // Až detekujeme kód, zavoláme callback a
                // ukončíme skener (Screen). Tady jen pro ukázku:
                onScanned(detectedValue)
            })
        }
    }
}

/**
 * Zabaleno do samostatné composable, která vykreslí [PreviewView] (CameraX)
 * a nastaví [ImageAnalysis] pro MLKit BarcodeScanning.
 */
@SuppressLint("UnsafeOptInUsageError") // kvůli .setAnalyzer, MLKit analyzátoru
@Composable
fun CameraPreviewWithAnalyzer(
    onScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Uloží si CameraProvider (potřebný pro binding do lifecycle)
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    // Executor pro zpracování kamerových snímků (mimo hlavní UI thread)
    val cameraExecutor: ExecutorService = remember {
        Executors.newSingleThreadExecutor()
    }

    // Náš "UI prvek" pro náhled z kamery
    val previewView = remember { PreviewView(context) }

    // Načítání CameraProvideru na pozadí
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    LaunchedEffect(cameraProviderFuture) {
        cameraProvider = cameraProviderFuture.get()
    }

    // Když máme cameraProvider, můžeme nastavit preview a analyzer
    LaunchedEffect(cameraProvider) {
        val provider = cameraProvider ?: return@LaunchedEffect

        // Vytvoříme preview use-case
        val preview = Preview.Builder()
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }

        // Vytvoříme analyzér pro MLKit (čárové / QR kódy)
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysisUseCase ->
                analysisUseCase.setAnalyzer(cameraExecutor, { imageProxy ->
                    processImageProxy(
                        imageProxy = imageProxy,
                        context = context,
                        onScanned = onScanned
                    )
                })
            }

        try {
            // Odpojíme předchozí use-cases
            provider.unbindAll()

            // Připojíme preview + analysis. Použijeme zadní kameru (default).
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Vykreslení PreviewView do Composu
    // (AndroidView zabalí nativní View do Compose)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.weight(0.9f) // zbytek prostoru pod AppBar
    ) {
        androidx.compose.ui.viewinterop.AndroidView(
            factory = {
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Funkce, která převede [ImageProxy] na MLKit [InputImage],
 * zavolá MLKit BarcodeScanner a při úspěchu předá kódy do callbacku `onScanned(...)`.
 */
@SuppressLint("UnsafeOptInUsageError") // kvůli ImageProxy.toImage()
private fun processImageProxy(
    imageProxy: ImageProxy,
    context: Context,
    onScanned: (String) -> Unit
) {
    // Získáme rotaci snímku
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    val mediaImage = imageProxy.image

    if (mediaImage != null) {
        // Zabalíme do InputImage
        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        // MLKit barcode scanner
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // barcodes je list detekovaných kódů
                if (barcodes.isNotEmpty()) {
                    // Může jich být víc - vezměme třeba jen první
                    val firstBarcode = barcodes.first()
                    val rawValue = firstBarcode.rawValue // textový obsah

                    rawValue?.let { value ->
                        // Máme nalezený kód
                        Log.d("ScannerScreen", "Nalezený kód: $value")
                        onScanned(value)
                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
            .addOnCompleteListener {
                // Nezapomeň vždy uzavřít imageProxy!
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}