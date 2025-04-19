package com.example.fridgetracker_001.obrazovky

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.mojeUI.PotravinaFormBoxWithConstraints
import com.example.fridgetracker_001.viewmodel.CodeViewModel
import com.example.fridgetracker_001.viewmodel.NakupViewModel
import com.example.fridgetracker_001.viewmodel.PotravinaViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel
import kotlinx.coroutines.launch

/*
@Composable
fun EditPotravinaObrazovka(
    potravinaId: Int,
    potravinaviewmodel: PotravinaViewModel,
    codeviewmodel: CodeViewModel,
    navController: NavController
) {
    val potravinaFlow = potravinaviewmodel.getPotravinaFlowById(potravinaId)
    val potravina = potravinaFlow.collectAsState(initial = null).value

    if (potravina == null) {
        // Ještě se nenačetl z DB. Můžete zobrazit Loading.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color.Black,
                trackColor = Color.White,
                strokeWidth = 4.dp,
            )
        }
    } else {

        // 2) Lokální stav pro potravinu, budeme ho měnit
        //var potravinaState by remember { mutableStateOf(potravina) }
        var potravinaState = potravinaviewmodel.pridavanaPotravina

        // 2a) Pohlídáme si `scannedCode` z NavBackStack (po návratu ze skeneru)
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        val scannedCodeFlow = savedStateHandle?.getStateFlow<String>("scannedCode", "")
        val newScannedValue by scannedCodeFlow?.collectAsState() ?: remember { mutableStateOf("") }

        // 2b) Jakmile je v "savedStateHandle" nově naskenovaný kód → uložíme ho do potravinaState
        LaunchedEffect(newScannedValue) {
            if (newScannedValue.isNotEmpty()) {
                potravinaState = potravinaState.copy(code = newScannedValue)
                savedStateHandle?.set("scannedCode", "") // vynuluj, aby se znovu netriggerovalo
            }
        }

        // 2c) Pokaždé, když se změní potravinaState.code, zavoláme CodeViewModel pro načtení detailů
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

        // Až se entity načte, zobraz formulář:
        PotravinaFormBoxWithConstraints(
            potravina = potravinaState,
            onPotravinaChange = { updated ->
                potravinaState = updated
            },
            isEdit = true,
            isAdd = false,
            onSubmit = {
                potravinaviewmodel.ulozitPotravinuSCode(potravinaState)
                navController.popBackStack()
            },
            onCancel = {
                navController.popBackStack()
            },
            onDelete = {
                potravinaviewmodel.smazatPotravinu(potravinaState)
                navController.popBackStack()
            },
            // Jednoduchý callback místo navController.navigate:
            onScanBarcodeClick = {
                val returnRoute = "editPotravinu"  // až se vrátím, vím kam to patří
                val param = potravinaState.id
                navController.navigate("scanner?returnRoute=$returnRoute&param=$param")
            }
        )
    }
}

 */



@Composable
fun EditPotravinaObrazovka2(
    potravinaId: Int,
    barcodePolozky: String = "",
    potravinaViewModel: PotravinaViewModel,
    seznamViewModel: SeznamViewModel,
    navController: NavController,
    nakupViewModel: NakupViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Odposlouchávání eventů
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
    // 1) Načteme do VM, pokud to ještě není
    LaunchedEffect(potravinaId, barcodePolozky) {
        potravinaViewModel.initEditedPotravina(potravinaId, barcodePolozky)
    }

    // 2) Posloucháme stav z VM
    val potravinaState = potravinaViewModel.editedPotravina

    // 3) Čekáme, až se potravina z DB načte
    if (potravinaState == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {

        BackHandler {
            // Stejné jako onCancel:
            potravinaViewModel.discardEditedPotravina()
            navController.navigate("SkladObrazovka/${potravinaState.skladId}")
        }

        val isAdd = potravinaViewModel.showDruhDialog

        // 5) Vykreslíme formulář s parametry:
        PotravinaFormBoxWithConstraints(
            potravina = potravinaState,
            onPotravinaChange = { updated ->
                // Při každé změně nastavujeme do VM
                potravinaViewModel.updateEditedPotravina(updated)
            },
            isEdit = true,  // Zde "Edit" = true
            isAdd = isAdd,  // Není to přidání
            onDialogChange = { value ->
                if (value) {
                    potravinaViewModel.openDruhDialog()
                } else {
                    potravinaViewModel.closeDruhDialog()
                }
            },
            onSubmit = {
                // Uložíme změny do DB
                potravinaViewModel.ulozitZmenyPotravina()
                // a vrátíme se
                navController.navigate("SkladObrazovka/${potravinaState.skladId}")
            },
            onCancel = {
                // Zahodíme změny (pokud chcete)
                potravinaViewModel.discardEditedPotravina()
                // a vrátíme se
                navController.navigate("SkladObrazovka/${potravinaState.skladId}")
            },
            onDelete = {
                // Smažeme potravinu
                potravinaViewModel.smazatEditedPotravina()
                // a vrátíme se
                navController.navigate("SkladObrazovka/${potravinaState.skladId}")
            },
            pridejNaSeznam = {
                val polozka = PolozkyEntity(nazev = potravinaState.nazev, kategorie = potravinaState.druh)
                val currentId = nakupViewModel.currentNakup.value?.id ?: 1
                seznamViewModel.pridatZKatalogu(polozka, nakupId = currentId)
            },
            onScanBarcodeClick = {
                // Navigujeme na skener
                val newRouteAfterScan = "editPotravinu/${potravinaId}?barcode="
                navController.navigate("scanner?returnRoute=$newRouteAfterScan")
            },
            snackbarHostState = snackbarHostState
        )
    }
}
