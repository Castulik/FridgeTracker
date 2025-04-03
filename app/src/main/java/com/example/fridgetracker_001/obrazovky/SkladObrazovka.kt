package com.example.fridgetracker_001.obrazovky

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.mojeUI.AiPromptDialog
import com.example.fridgetracker_001.mojeUI.BottomBarAddPotravinu
import com.example.fridgetracker_001.mojeUI.BottomBarMultiSelect
import com.example.fridgetracker_001.mojeUI.MultiSelectTopBar
import com.example.fridgetracker_001.mojeUI.PotravinaItem
import com.example.fridgetracker_001.mojeUI.TopBar
import com.example.fridgetracker_001.mojeUI.NastaveniDialog
import com.example.fridgetracker_001.mojeUI.SortDialog
import com.example.fridgetracker_001.mojeUI.ViewTypeDialog
import com.example.fridgetracker_001.ui.theme.topmenu12
import com.example.fridgetracker_001.viewmodel.PotravinaViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel
import com.example.fridgetracker_001.viewmodel.SkladViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ViewType {
    LIST,
    SMALL_LIST,
    GRID
}

fun parseViewType(value: String): ViewType {
    return when (value) {
        "SMALL_LIST" -> ViewType.SMALL_LIST
        "GRID" -> ViewType.GRID
        else -> ViewType.LIST
    }
}

// Extension funkce pro Map<String, Boolean>, která vrací JSON řetězec
fun Map<String, Boolean>.toJsonString(): String {
    return Gson().toJson(this)
}

// Extension funkce pro String, která se pokusí převést JSON řetězec na Map<String, Boolean>
fun String.toCategoryExpansionMap(): Map<String, Boolean> {
    return try {
        // Získáme typ, který reprezentuje Map<String, Boolean>
        val type = object : TypeToken<Map<String, Boolean>>() {}.type
        // Použijeme Gson k převedení JSON řetězce na mapu
        Gson().fromJson(this, type)
    } catch (e: Exception) {
        // Pokud dojde k chybě (například neplatný JSON), vrátíme prázdnou mapu
        emptyMap()
    }
}

@Composable
fun SkladObrazovka2(
    skladId: Int,
    skladViewModel: SkladViewModel,
    navController: NavController,
    potravinaViewModel: PotravinaViewModel,
    seznamViewModel: SeznamViewModel
) {
    LaunchedEffect(skladId) {
        skladViewModel.nactiSkladPodleId(skladId)
        potravinaViewModel.nactiPotravinyPodleIdSkladu(skladId)
    }

    BackHandler {
        navController.navigate("mojepotraviny/?barcode=")
    }

    val sklad = skladViewModel.skladState.collectAsState().value
    val potravinaList = potravinaViewModel.potravinaList.collectAsState().value
    val skladList = skladViewModel.skladList.collectAsState().value

    var promptDialogVisible by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var viewTypeDialogVisible by remember { mutableStateOf(false) }
    var nastaveniDialogVisible by remember { mutableStateOf(false) }
    var sortDialogVisible by remember { mutableStateOf(false) }
    var localViewType by remember { mutableStateOf(ViewType.LIST) }   // Základní výchozí hodnota
    // Jakmile se změní sklad, zaktualizuj localViewType na to, co je v DB
    LaunchedEffect(sklad) {
        if (sklad != null) {
            localViewType = parseViewType(sklad.viewType)
        }
    }
    val columnsCount = when (localViewType) {
        ViewType.LIST, ViewType.SMALL_LIST -> 1
        ViewType.GRID -> 4
    }

    // Všechny možné kategorie
    val allCategories = listOf(
        "Mražené", "Trvanlivé", "Ovoce a Zelenina", "Mléčné výrobky", "Maso a Ryby",
        "Pečivo", "Vejce", "Obiloviny a luštěniny",
        "Uzeniny a lahůdky", "Nápoje", "Hotová jídla", "Ostatní"
    )

    // Rozdělení potravin dle druhu
    val mapByCategory = potravinaList.groupBy { it.druh }

    // Tento kód definuje stav rozbalení/skrytí kategorií, kde výchozí stav je "rozbaleno" (true) pro všechny kategorie.
    // Hodnota se načítá z entity "sklad" – pokud existuje a obsahuje ne-prázdný JSON, ten se převede na mapu.
    val expandedCategories = remember(sklad) {
        // Vytvoříme dočasnou prázdnou mutable mapu, kam uložíme výchozí nebo načtené hodnoty pro každou kategorii.
        val initialMap: MutableMap<String, Boolean> = mutableMapOf()

        // Podmínka: Pokud máme platný objekt 'sklad' a pole 'categoryExpansionState' není prázdné...
        if (sklad != null && sklad.categoryExpansionState.isNotBlank()) {
            // Převedeme JSON uložený v 'categoryExpansionState' na mapu (String -> Boolean) a uděláme z ní mutable kopii.
            val parsed = sklad.categoryExpansionState.toCategoryExpansionMap().toMutableMap()

            // Pro každou kategorii ze seznamu 'allCategories' (referenční seznam všech kategorií, které by měly být zobrazeny)
            allCategories.forEach { category ->
                // Pokud načtená mapa "parsed" neobsahuje záznam pro danou kategorii,
                // nastavíme výchozí hodnotu true (tj. kategorie bude rozbalená).
                if (!parsed.containsKey(category)) {
                    parsed[category] = true
                }
            }
            // Do naší dočasné mapy "initialMap" vložíme všechny položky z mapy "parsed".
            initialMap.putAll(parsed)
        } else {
            // Pokud není objekt 'sklad' dostupný nebo je pole 'categoryExpansionState' prázdné,
            // pro každou kategorii ve 'allCategories' nastavíme výchozí hodnotu true.
            allCategories.forEach { category ->
                initialMap[category] = true
            }
        }
        // Vytvoříme speciální mutable mapu pro Compose (mutableStateMapOf), která dokáže sledovat změny a způsobovat překreslení UI.
        // Do této mapy vložíme všechny položky z naší dočasné mapy 'initialMap'.
        mutableStateMapOf<String, Boolean>().apply { putAll(initialMap) }
    }

    // Stav a vybraná položka pro zobrazení akcí v dolním panelu
    var multiSelectMode by remember { mutableStateOf(false) }
    val selectedPotraviny = remember { mutableStateListOf<PotravinaEntity>() }

    var selectPotravinyForAi by remember { mutableStateOf(false) }
    var aiPromptTitle by remember { mutableStateOf("Co mám uvařit z těchto potravin: ") }
    var aiPotravinyText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Color.Transparent,
        // TopBar: mění se podle multiSelectMode
        topBar = {
            when{
                multiSelectMode -> {
                    MultiSelectTopBar(
                        selectedCount = selectedPotraviny.size,
                        onBackClick = {
                            // Zrušit multi-select a vyčistit seznam
                            multiSelectMode = false
                            selectedPotraviny.clear()
                            selectPotravinyForAi = false
                        },
                        ai = selectPotravinyForAi,
                    )
                }
                else ->
                    TopBar(
                    nazevSkladu = sklad?.nazev ?: "",
                    onBackClick = { navController.navigate("mojepotraviny/?barcode=") },
                    onAiClick = { promptDialogVisible = true },
                    onEditClick = { multiSelectMode = true },
                    menuExpanded = menuExpanded,
                    onMenuExpandedChange = { menuExpanded = it },
                    onSortClicked = { sortDialogVisible = true },
                    onViewTypeClicked = { viewTypeDialogVisible = true },
                    onSettingsClicked = { nastaveniDialogVisible = true }
                    )
            }
        },
        // ==> BottomBar <==
        bottomBar = {
            when {
                multiSelectMode -> {
                    BottomBarMultiSelect(
                        skladList = skladList,
                        onMove = { vybranySklad ->
                            // Tady provedeš přesun všech 'selectedPotraviny' do vybraného skladu
                            // Třeba:
                            selectedPotraviny.forEach { potravina ->
                                potravinaViewModel.presunPotravinuDoJinehoSkladu(
                                    potravina.id,
                                    vybranySklad.id
                                )
                            }
                            // Po dokončení:
                            multiSelectMode = false
                            selectedPotraviny.clear()
                            potravinaViewModel.nactiPotravinyPodleIdSkladu(skladId)
                        },
                        onCopy = {
                            selectedPotraviny.forEach { original ->
                                val copy = original.copy(id = 0)
                                potravinaViewModel.pridatPotravinu(copy)
                            }
                            multiSelectMode = false
                            selectedPotraviny.clear()
                        },
                        onDelete = {
                            selectedPotraviny.forEach { potravinaViewModel.smazatPotravinu(it) }
                            multiSelectMode = false
                            selectedPotraviny.clear()
                            potravinaViewModel.nactiPotravinyPodleIdSkladu(skladId)
                        },
                        pridejNaSeznam = {
                            selectedPotraviny.forEach{
                                val newItem = SeznamEntity(nazev = it.nazev, kategorie = it.druh, nakupId = 0)
                                seznamViewModel.pridatPolozku(newItem)
                            }
                        },
                        ai = selectPotravinyForAi,
                        onConfirmClick = {
                            // posbírá názvy
                            val potravinyString = selectedPotraviny.joinToString(", ") { it.nazev }
                            // reset multiSelect
                            multiSelectMode = false
                            selectedPotraviny.clear()
                            selectPotravinyForAi = false
                            // otevřít AI dialog
                            aiPotravinyText = potravinyString
                            promptDialogVisible = true
                        }
                    )
                }
                else ->{
                    BottomBarAddPotravinu (
                        onAddClick = { navController.navigate("pridatPotravinu/$skladId") },
                        onQrClick = {
                            val routeAfterScan = "pridatPotravinu/$skladId?barcode="
                            navController.navigate("scanner?returnRoute=$routeAfterScan")
                        },
                    )
                }
            }
        }
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            val sortedCategories = when (sklad?.sortKategorie) {
                "ALPHABETICAL" -> allCategories.sorted()
                "COUNT" -> allCategories.sortedByDescending { mapByCategory[it]?.size ?: 0 }
                else -> allCategories // "DEFAULT"
            }

            sortedCategories.forEach { category ->

                val rawItems = mapByCategory[category].orEmpty()
                val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val itemsInThisCategory = when (sklad?.sortPotraviny) {
                    "DATE_EXPIRY" -> {
                        // Nejbližší expirace nahoru => sort ascending
                        rawItems.sortedBy { potravina ->
                            try {
                                LocalDate.parse(potravina.datumSpotreby, dateFormatter)
                            } catch (e: Exception) {
                                LocalDate.MAX
                            }
                        }
                    }

                    "DATE_ADDED" -> {
                        // Nejnovější přidané nahoru => sort descending
                        rawItems.sortedByDescending { potravina ->
                            try {
                                LocalDate.parse(potravina.datumPridani, dateFormatter)
                            } catch (e: Exception) {
                                LocalDate.MIN
                            }
                        }
                    }
                    else -> rawItems.sortedBy { it.nazev }
                }

                if (itemsInThisCategory.isNotEmpty()) {
                    // Nadpis kategorie - roztáhne se přes celou šířku
                    item(
                        span = { GridItemSpan(columnsCount) } // kategorie zabere všechny sloupce
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(topmenu12)
                                .clickable { // Přepneme stav pro tuto kategorii
                                    val newState = !(expandedCategories[category] ?: true)
                                    expandedCategories[category] = newState
                                    // Aktualizujeme stav v databázi
                                    sklad?.let { currentSklad ->
                                        val newMap = expandedCategories.toMap()
                                        skladViewModel.updateCategoryExpansionState(
                                            currentSklad.id,
                                            newMap
                                        )
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp),
                                    text = "$category (${itemsInThisCategory.size})",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                IconButton(
                                    onClick = {
                                        // Přepneme stav pro tuto kategorii
                                        val newState = !(expandedCategories[category] ?: true)
                                        expandedCategories[category] = newState
                                        // Aktualizujeme stav v databázi
                                        sklad?.let { currentSklad ->
                                            val newMap = expandedCategories.toMap()
                                            skladViewModel.updateCategoryExpansionState(
                                                currentSklad.id,
                                                newMap
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (expandedCategories[category] == true)
                                            Icons.Default.KeyboardArrowUp
                                        else
                                            Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (expandedCategories[category] == true)
                                            "Sbalit" else "Rozbalit",
                                    )
                                }
                            }
                        }
                    }

                    // Pokud je kategorie expanded, vykresli items
                    if (expandedCategories[category] == true) {
                        items(
                            itemsInThisCategory,
                            key = { it.id } // stabilní klíč
                        ) { potravina ->
                            // Zavoláme PotravinaItem
                            // a v parametru viewType = localViewType
                            PotravinaItem(
                                potravina = potravina,
                                viewType = localViewType,
                                smazatPotravina = {
                                    potravinaViewModel.smazatPotravinu(potravina)
                                },
                                pridejNaSeznam = {
                                    val newItem = SeznamEntity(nazev = potravina.nazev, kategorie = potravina.druh, nakupId = 0)
                                    seznamViewModel.pridatPolozku(newItem)
                                },
                                editPotravina = {
                                    navController.navigate("editPotravinu/${potravina.id}")
                                },
                                onTap = {
                                    if (multiSelectMode) {
                                        if (selectedPotraviny.contains(potravina)) {
                                            selectedPotraviny.remove(potravina)
                                            if (selectedPotraviny.isEmpty()) {
                                                multiSelectMode = false
                                                selectPotravinyForAi = false
                                            }
                                        } else {
                                            selectedPotraviny.add(potravina)
                                        }
                                    } else {
                                        navController.navigate("editPotravinu/${potravina.id}")
                                    }
                                },
                                onLongPress = {
                                    if (!multiSelectMode) {
                                        multiSelectMode = true
                                    }
                                    if (!selectedPotraviny.contains(potravina)) {
                                        selectedPotraviny.add(potravina)
                                    }
                                },
                                getDaysLeft = {
                                    potravinaViewModel.getDaysLeft(potravina)
                                },
                                isSelected = selectedPotraviny.contains(potravina),
                            )
                        }
                    }
                }
            }
        }

        // Dialog pro volbu vzhledu
        if (viewTypeDialogVisible) {
            ViewTypeDialog(
                viewTypeDialogVisible = { viewTypeDialogVisible = false },
                listChange = {
                    localViewType = ViewType.LIST
                    skladViewModel.nastavViewType(skladId, ViewType.LIST.name)
                },
                smallListChange = {
                    localViewType = ViewType.SMALL_LIST
                    skladViewModel.nastavViewType(skladId, ViewType.SMALL_LIST.name)
                },
                gridChange = {
                    localViewType = ViewType.GRID
                    skladViewModel.nastavViewType(skladId, ViewType.GRID.name)
                },
                localViewType = localViewType.name
            )
        }

        if (nastaveniDialogVisible) {
            NastaveniDialog(
                sklad = sklad,
                onSave = { newExp1, newExp2 ->
                    sklad?.let {
                        val updatedSklad = it.copy(expirace1 = newExp1, expirace2 = newExp2)
                        skladViewModel.aktualizovatSklad(updatedSklad)
                    }
                    nastaveniDialogVisible = false
                },
                onDismiss = {
                    nastaveniDialogVisible = false
                },
            )
        }

        if (sortDialogVisible) {
            SortDialog(
                sortDialogVisible = { sortDialogVisible = false },
                nameChange = { skladViewModel.nastavSortPotraviny(skladId, "NAME") },
                dateExpiryChange = { skladViewModel.nastavSortPotraviny(skladId, "DATE_EXPIRY")},
                dateAddedChange = { skladViewModel.nastavSortPotraviny(skladId, "DATE_ADDED") },
                alphabetChange = { skladViewModel.nastavSortKategorie(skladId, "ALPHABETICAL") },
                defaultChange = { skladViewModel.nastavSortKategorie(skladId, "DEFAULT") },
                countChange = { skladViewModel.nastavSortKategorie(skladId, "COUNT") }
            )
        }

        if (promptDialogVisible) {
            val clipboardManager = LocalClipboardManager.current
            AiPromptDialog(
                aiPromptTitle = aiPromptTitle,
                aiPromptTitleChange = { aiPromptTitle = it },
                aiPotravinyText = aiPotravinyText,
                aiPotravinyTextChange = { aiPotravinyText = it },
                potravinaList = potravinaList,
                getDaysLeft = { potravinaViewModel.getDaysLeft(it) },
                onManualSelect = {
                    promptDialogVisible = false
                    selectPotravinyForAi = true
                    multiSelectMode = true
                },
                onCopy = { clipboardText ->
                    // Tady zkopírujeme do schránky a třeba zobrazíme Snackbar
                    clipboardManager.setText(AnnotatedString(clipboardText))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Zkopírováno do schránky")
                    }
                },
                aiDialogVisible = {
                    promptDialogVisible = false
                }
            )
        }
    }
}