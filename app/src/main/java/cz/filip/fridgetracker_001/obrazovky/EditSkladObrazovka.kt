package cz.filip.fridgetracker_001.obrazovky


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import cz.filip.fridgetracker_001.mojeUI.SkladFormBoxWithConstraints
import cz.filip.fridgetracker_001.viewmodel.SkladViewModel
import java.io.File

@Composable
fun EditSkladObrazovka2(
    navController: NavController,
    skladViewModel: SkladViewModel,
    skladId: Int,
) {
    val skladFlow = skladViewModel.getSkladFlowById(skladId)
    val sklad = skladFlow.collectAsState(initial = null).value
    val skladList by skladViewModel.skladList.collectAsState()
    val maxPoradi = skladList.maxOfOrNull { it.poradi } ?: 1

    // Pokud se teprve načítá, zobrazíme "Loading"
    if (sklad == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color.Black,
                trackColor = Color.Black,
                strokeWidth = 4.dp,
            )
        }
        return
    }

    // ====================== LOKÁLNÍ STAVY A LOGIKA ======================
    var skladState by remember { mutableStateOf(sklad) }

    val context = LocalContext.current

    // 1) Soubor, kam se vyfocená fotka uloží ve full-size (nebo tak velké,
    //    jak ji kamera umí).
    val tmpFile = remember {
        File(
            context.getExternalFilesDir(null),
            "tmp_photo_${System.currentTimeMillis()}.jpg"
        )
    }

    // 2) Uri pro FileProvider
    val photoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tmpFile
        )
    }

    // 3) Launcher pro plnohodnotné foto (vrací boolean success)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // 1) Uložíme si starou cestu, kterou chceme případně smazat
            val oldPhotoPath = skladState.iconPath

            // 2) Zpracujeme novou vyfocenou fotku (změna velikosti, uložení atd.)
            val targetWidth = 800
            val originalBitmap = BitmapFactory.decodeFile(tmpFile.absolutePath)
            if (originalBitmap != null) {
                val scaled = Bitmap.createScaledBitmap(
                    originalBitmap,
                    targetWidth,
                    (originalBitmap.height * targetWidth / originalBitmap.width).toInt(),
                    true
                )

                // Uložíme do interního úložiště
                val newPath = saveBitmapToInternalStorage(context, scaled)

                // 3) Nastavíme novou cestu do stavu
                skladState = skladState.copy(
                    iconPath = newPath,
                    preferovane = true
                )

                // 4) Smažeme dočasný soubor tmpFile (pokud ho nepotřebujete dál)
                tmpFile.delete()

                // 5) Nakonec smažeme starou fotku z disku (pokud byla nějaká)
                oldPhotoPath?.let { deleteFilePath(it) }
            }
        } else {
            // Uživatel focení zrušil
            Toast.makeText(context, "Fotka nebyla pořízena", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission launcher pro kameru
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(photoUri) // teprve teď můžeme spustit foťák
        } else {
            // Uživatel odmítl
            Toast.makeText(context, "Kamera zamítnuta", Toast.LENGTH_SHORT).show()
        }
    }

    // Funkce pro "vyfotit"
    fun onTakePhotoClick() {
        // Zkontrolujeme, zda je permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(photoUri)
        } else {
            // Požádáme
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Funkce pro "smazat fotku"
    fun onCameraPhotoDelete() {
        deleteFilePath(skladState.iconPath)
        skladState = skladState.copy(iconPath = null, preferovane = false)
    }

    // ====================== VOLÁME ČISTĚ UI KOMPONENTU ======================
    SkladFormBoxWithConstraints(
        // Posíláme stavy
        sklad = skladState,
        onSkladChange = { skladState = it },
        maxPoradi = maxPoradi,
        isEdit = true,
        onCameraPhotoDelete = { onCameraPhotoDelete() },
        onTakePhotoClick = { onTakePhotoClick() },
        onConfirm = {
            skladViewModel.updateSkladFullWithReorder(skladState, skladState.poradi)
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        },
        onDelete = {
            skladViewModel.smazatSklad(skladState)
            navController.popBackStack()
        },
    )
}
