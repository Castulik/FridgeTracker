package cz.filip.fridgetracker_001.obrazovky

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import cz.filip.fridgetracker_001.data.SkladIcon
import cz.filip.fridgetracker_001.data.entities.SkladEntity
import cz.filip.fridgetracker_001.mojeUI.SkladFormBoxWithConstraints
import cz.filip.fridgetracker_001.viewmodel.SkladViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun PridatSkladObrazovka2(
    navController: NavController,
    skladViewModel: SkladViewModel
) {
    val skladList by skladViewModel.skladList.collectAsState()
    val maxPoradi = skladList.maxOfOrNull { it.poradi } ?: 0

    // Výchozí "prázdný" sklad
    val defaultSklad = SkladEntity(
        nazev = "",
        icon = SkladIcon.LEDNICKA,
        poradi = maxPoradi + 1,
        preferovane = false
    )

    // =========== LOKÁLNÍ STAVY ===========
    var skladState by remember { mutableStateOf(defaultSklad) }

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

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(context, "Kamera zamítnuta", Toast.LENGTH_SHORT).show()
        }
    }

    // Funkce pro vyfocení
    fun onTakePhotoClick() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(photoUri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Mazání fotky
    fun onCameraPhotoDelete() {
        deleteFilePath(skladState.iconPath)
        skladState = skladState.copy(iconPath = null, preferovane = false)
    }

    // ===================== KOMPONENTA UI =====================
    SkladFormBoxWithConstraints(
        sklad = skladState,
        onSkladChange = { skladState = it },
        maxPoradi = maxPoradi,
        isEdit = false,
        onCameraPhotoDelete = { onCameraPhotoDelete() },
        onTakePhotoClick = { onTakePhotoClick() },
        onConfirm = {
            skladViewModel.insertSkladWithReorder(skladState, skladState.poradi)
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        },
    )
}

fun saveBitmapToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    folderName: String = "skladVyfocene"
): String {
    // Vytvoříme složku (pokud neexistuje)
    val directory = File(context.filesDir, folderName)
    if (!directory.exists()) {
        directory.mkdirs()
    }

    // Vytvoříme soubor se jménem, např. "icon_1675936082.png"
    val fileName = "icon_${System.currentTimeMillis()}.jpg"
    val newFile = File(directory, fileName)

    // Uložíme bitmapu do PNG
    FileOutputStream(newFile).use { fos ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        fos.flush()
    }

    return newFile.absolutePath  // Cesta, kterou pak uložíš do databáze
}

/**
 * Smaže soubor podle cesty (pokud existuje).
 * @return true, pokud se úspěšně smazal; false, pokud soubor neexistuje nebo se nepovedlo smazat.
 */
fun deleteFilePath(filePath: String?): Boolean {
    if (filePath.isNullOrBlank()) return false
    val file = File(filePath)
    return if (file.exists()) file.delete() else false
}
