package com.example.fridgetracker_001.obrazovky

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgetracker_001.viewmodel.SkladViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.example.fridgetracker_001.mojeUI.GlobalSearchBar
import com.example.fridgetracker_001.mojeUI.SkladItem
import com.example.fridgetracker_001.viewmodel.PotravinaViewModel
import com.example.fridgetracker_001.R




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MojePotravinyObrazovka(
    navController: NavController,
    skladViewModel: SkladViewModel,
    potravinaViewModel: PotravinaViewModel,
    barcode: String = ""
) {
    val skladList by skladViewModel.skladList.collectAsState()

    val druhTranslations = mapOf(
        R.string.kind_frozen to listOf(stringResource(R.string.kind_frozen), "frozen", "mražené"),
        R.string.kind_nonperishable to listOf(stringResource(R.string.kind_nonperishable), "nonperishable", "trvanlivé"),
        R.string.kind_fruit_veg to listOf(stringResource(R.string.kind_fruit_veg), "fruit", "vegetables", "ovoce", "zelenina"),
        R.string.kind_dairy to listOf(stringResource(R.string.kind_dairy), "dairy", "mléčné výrobky"),
        R.string.kind_meat_fish to listOf(stringResource(R.string.kind_meat_fish), "meat", "fish", "maso", "ryby"),
        R.string.kind_bakery to listOf(stringResource(R.string.kind_bakery), "bakery", "pečivo"),
        R.string.kind_eggs to listOf(stringResource(R.string.kind_eggs), "eggs", "vejce"),
        R.string.kind_grains_legumes to listOf(stringResource(R.string.kind_grains_legumes), "grains", "legumes", "obiloviny", "luštěniny"),
        R.string.kind_deli to listOf(stringResource(R.string.kind_deli), "deli", "uzeniny", "lahůdky"),
        R.string.kind_drinks to listOf(stringResource(R.string.kind_drinks), "drinks", "nápoje"),
        R.string.kind_ready_meals to listOf(stringResource(R.string.kind_ready_meals), "ready meals", "hotová jídla"),
        R.string.kind_other to listOf(stringResource(R.string.kind_other), "other", "ostatní")
    )

    // --- LOGIKA SEARCH BARU (stav + reakce na sken) ---
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(barcode) {
        if (barcode.isNotEmpty()) {
            query = barcode
            active = true
        }
    }

    // Načtení všech potravin a skladů
    val allPotraviny = potravinaViewModel.allPotravinyFlow.collectAsState().value
    val allSklady = skladViewModel.skladList.collectAsState().value

    val filteredPotraviny = remember(query, allPotraviny, druhTranslations) {
        if (query.isBlank()) {
            emptyList()
        } else {
            allPotraviny.filter { potravina ->
                val druhKeywords = potravina.druh?.let { druhTranslations[it] }.orEmpty()

                potravina.nazev.contains(query, ignoreCase = true) ||
                        potravina.code.contains(query, ignoreCase = true) ||
                        druhKeywords.any { it.contains(query, ignoreCase = true) }
            }
        }
    }


    Scaffold(
        topBar = {
            GlobalSearchBar(
                query = query,
                onQueryChange = { newValue ->
                    query = newValue
                },
                active = active,
                onActiveChange = { newActive ->
                    active = newActive
                    if (!newActive) {
                        query = ""
                        focusManager.clearFocus()
                    }
                },
                onClearQuery = {
                    query = ""
                },
                onScanClick = {
                    val newRouteAfterScan = "mojepotraviny/?barcode="
                    navController.navigate("scanner?returnRoute=$newRouteAfterScan")
                },
                filteredItems = filteredPotraviny,
                getSkladName = { skladId ->
                    val thisSklad = allSklady.firstOrNull { it.id == skladId }
                    thisSklad?.nazev ?: "Neznámé"
                },
                getDaysLeft = { potravina ->
                    potravinaViewModel.getDaysLeft(potravina)
                },
                onItemClick = { potravina ->
                    navController.navigate("editPotravinu/${potravina.id}")
                },
            )
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 4.dp
                )
        ) {
            // Seznam skladů (může být cokoliv dalšího)
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(skladList) { sklad ->
                    SkladItem(
                        sklad = sklad,
                        onNavigate = { skladId ->
                            navController.navigate("SkladObrazovka/$skladId")
                        },
                        nastaveniSkladu = { skladId ->
                            navController.navigate("nastaveniskladu/$skladId")
                        }
                    )
                }
            }
        }
    }
}
