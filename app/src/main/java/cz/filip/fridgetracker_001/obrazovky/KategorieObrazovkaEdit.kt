package cz.filip.fridgetracker_001.obrazovky

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import cz.filip.fridgetracker_001.R
import cz.filip.fridgetracker_001.data.KindOptionEnum
import cz.filip.fridgetracker_001.mojeUI.KategorieForm
import cz.filip.fridgetracker_001.viewmodel.PotravinaViewModel
import kotlinx.coroutines.launch

@Composable
fun KategorieObrazovkaEdit(
    potravinaViewModel: PotravinaViewModel,
    potravinaId: Int,
    barcodePolozky: String = "",
    navController: NavController
){

    LaunchedEffect(potravinaId, barcodePolozky) {
        potravinaViewModel.initEditedPotravina(potravinaId, barcodePolozky)
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

    val potravinaState = potravinaViewModel.editedPotravina

    if (potravinaState == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        KategorieForm(
            onCancel = {
                navController.navigate("editPotravinu/${potravinaState.id}")
            },
            onNavigate = { icon ->
                potravinaViewModel.updateEditedPotravina(potravinaState.copy(potravinaIkona = icon))
                navController.navigate("editPotravinu/${potravinaState.id}")
            },
            onOptionSelected = { update ->
                potravinaViewModel.updateEditedPotravina(potravinaState.copy(druh = update.name))
            },
            selectedOption = KindOptionEnum.fromString(potravinaState.druh),
            selectedIcon = potravinaState.potravinaIkona,
            title = stringResource(R.string.change_category),
            snackbarHostState = snackbarHostState
        )
    }
}