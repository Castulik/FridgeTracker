package com.example.fridgetracker_001.obrazovky

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.mojeUI.PotravinaFormBoxWithConstraints
import com.example.fridgetracker_001.viewmodel.CodeViewModel
import com.example.fridgetracker_001.viewmodel.PotravinaViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

/*
@Composable
fun PridatPotravinuObrazovka(
    skladId: Int,
    nazevPolozky: String = "",
    potravinaViewModel: PotravinaViewModel,
    codeviewmodel: CodeViewModel,
    navController: NavController
) {
    val defaultPotravina = PotravinaEntity(
        nazev = nazevPolozky,
        datumSpotreby = "",
        datumPridani = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
        potravinaIconaId = R.drawable.food_basic,
        mnozstvi = "",
        vaha = "",
        jednotky = "g",
        skladId = skladId, // to si pak při uložení nastavím podle aktuálního skladu
        poznamka = "",
        druh = "",
        code = ""
    )
    // 2) Lokální stav
    var potravinaState by rememberSaveable { mutableStateOf(defaultPotravina) }

    // 2a) Čteme naskenovaný kód z NavBackStack
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val scannedCodeFlow = savedStateHandle?.getStateFlow<String>("scannedCode", "")
    val newScannedValue by scannedCodeFlow?.collectAsState() ?: remember { mutableStateOf("") }

    // 2b) Pokud se vracím od skeneru, nastav do potravinaState
    LaunchedEffect(newScannedValue) {
        if (newScannedValue.isNotEmpty()) {
            potravinaState = potravinaState.copy(code = newScannedValue)
            savedStateHandle?.set("scannedCode", "")
        }
    }

    // 2c) Jakmile se změní code, načítáme z CodeViewModel
    LaunchedEffect(potravinaState.code) {
        if (potravinaState.code.isNotEmpty()) {
            val codeEntity = codeviewmodel.getCodeEntityByBarcode(potravinaState.code)
            if (codeEntity != null) {
                potravinaState = potravinaState.copy(
                    nazev = codeEntity.nazev,
                    druh = codeEntity.druh,
                    potravinaIconaId = codeEntity.potravinaIconaId
                )
            }
        }
    }

    // 3) Vykreslení "čistého" formuláře
    PotravinaFormBoxWithConstraints(
        potravina = potravinaState,
        onPotravinaChange = { updated ->
            potravinaState = updated
        },
        isEdit = false,
        isAdd = true,
        onSubmit = {
            potravinaViewModel.ulozitPotravinuSCode(potravinaState)
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        },
        onDelete = {}, // tady není potřeba mazat
        onScanBarcodeClick = {
            val returnRoute = "pridatPotravinu"
            // param = např. skladId, aby ses mohl vrátit do "správné" obrazovky
            navController.navigate("scanner?returnRoute=$returnRoute&param=$skladId")
        }
    )
}

 */

@Composable
fun PridatPotravinuObrazovka2(
    skladId: Int,
    nazevPolozky: String = "",
    barcodePolozky: String = "",
    potravinaViewModel: PotravinaViewModel,
    navController: NavController
) {
    // 1) Napojené na UI eventy z ViewModelu
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        potravinaViewModel.uiEvent.collect { event ->
            when (event) {
                is PotravinaViewModel.PotravinaUiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
    // 1) Při prvním zobrazení inicializujeme "pridavanaPotravina"
    LaunchedEffect(Unit) {
        // Pokud nechcete při každém znovuvykreslení "resetovat",
        // podmíněte to třeba na isAlreadyInited vlajku ve ViewModelu,
        // abyste jednou nastavili a pak už to neměnili.
        potravinaViewModel.initPridavanaPotravina(skladId, nazevPolozky, barcodePolozky)
    }
    // 2) Odebíráme stav potraviny z ViewModelu:
    val potravinaState = potravinaViewModel.pridavanaPotravina

    BackHandler {
        // Vaše akce, která se má stát při stisku HW tlačítka Back
        // Třeba to samé, co onCancel:
        potravinaViewModel.resetPridavanaPotravina()
        navController.navigate("SkladObrazovka/$skladId")
    }

    val isAdd = potravinaViewModel.shouldShowDialog()

    // 4) Teď vykreslete formulář (TextFieldy) a reagujte na změny
    PotravinaFormBoxWithConstraints(
        potravina = potravinaState,        // čteme z VM
        onPotravinaChange = { updated ->   // při editaci userem
            potravinaViewModel.updatePridavanaPotravina(updated)
        },
        isEdit = false,
        isAdd = isAdd,
        onDialogChange = { value ->
            if (value) {
                potravinaViewModel.openDruhDialog()
            } else {
                potravinaViewModel.closeDruhDialog()
            }
        },
        onSubmit = {
            potravinaViewModel.closeDruhDialog()
            // 5) Uložení do DB + resetPridavanePotraviny
            potravinaViewModel.ulozitPridavanouPotravinu()
            // a návrat
            navController.navigate("SkladObrazovka/$skladId")
        },
        onCancel = {
            potravinaViewModel.resetPridavanaPotravina()
            navController.navigate("SkladObrazovka/$skladId")
        },
        onDelete = {}, // nepotřebujete
        onScanBarcodeClick = {
            // Otevřít skener
            val newRouteAfterScan = "pridatPotravinu/$skladId?barcode="
            navController.navigate("scanner?returnRoute=$newRouteAfterScan")
        },
        snackbarHostState = snackbarHostState
    )
}
