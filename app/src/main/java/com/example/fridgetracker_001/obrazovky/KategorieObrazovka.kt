package com.example.fridgetracker_001.obrazovky

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.KindOptionEnum
import com.example.fridgetracker_001.mojeUI.KategorieForm
import com.example.fridgetracker_001.viewmodel.PotravinaViewModel
import kotlinx.coroutines.launch

@Composable
fun KategorieObrazovka(
    potravinaViewModel: PotravinaViewModel,
    skladId: Int,
    nazevPolozky: String = "",
    kategoriePolozky: String = "",
    barcodePolozky: String = "",
    fromScreen: String,
    navController: NavController
){
    LaunchedEffect(Unit) {
        potravinaViewModel.initPridavanaPotravina(skladId, nazevPolozky, kategoriePolozky, barcodePolozky)
    }

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

    val potravinaState = potravinaViewModel.pridavanaPotravina

    KategorieForm(
        onCancel = {
            if (fromScreen == "sklad") {
                potravinaViewModel.resetPridavanaPotravina()
                navController.navigate("SkladObrazovka/$skladId")
            } else
                navController.popBackStack()
        },
        onNavigate = { icon ->
            navController.navigate("pridatPotravinu/$skladId")
            potravinaViewModel.updatePridavanaPotravina(potravinaState.copy(potravinaIkona = icon))
        },
        onOptionSelected = { update ->
            potravinaViewModel.updatePridavanaPotravina(potravinaState.copy(druh = update.name))
        },
        selectedOption = KindOptionEnum.fromString(potravinaState.druh),
        selectedIcon = potravinaState.potravinaIkona,
        title = stringResource(R.string.select_category),
        snackbarHostState = snackbarHostState
    )
}