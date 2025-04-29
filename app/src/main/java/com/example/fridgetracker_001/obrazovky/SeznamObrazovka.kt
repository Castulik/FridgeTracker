package com.example.fridgetracker_001.obrazovky

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.KindOptionEnum
import com.example.fridgetracker_001.data.SortCategoryOption
import com.example.fridgetracker_001.data.SortOption
import com.example.fridgetracker_001.data.ViewTypeNakup
import com.example.fridgetracker_001.data.entities.NakupEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.mojeUI.AddItemDialog
import com.example.fridgetracker_001.mojeUI.NakupAlert
import com.example.fridgetracker_001.mojeUI.SeznamTopBar
import com.example.fridgetracker_001.mojeUI.SkladDialog
import com.example.fridgetracker_001.mojeUI.SortDialog
import com.example.fridgetracker_001.mojeUI.ViewTypeDialog
import com.example.fridgetracker_001.viewmodel.NakupViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel
import com.example.fridgetracker_001.viewmodel.SkladViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SeznamObrazovka2(
    seznamViewModel: SeznamViewModel,
    skladViewModel: SkladViewModel,
    navController: NavController,
    nakupViewModel: NakupViewModel
) {
    var skladDialog by remember { mutableStateOf(false) }
    var nakupDialog by remember { mutableStateOf(false) }
    var nastaveniDialog by remember { mutableStateOf(false) }
    var sortDialog by remember { mutableStateOf(false) }
    var viewTypeDialog by remember { mutableStateOf(false) }

    var selectedItem by remember { mutableStateOf("") }
    val skladList by skladViewModel.skladList.collectAsState()
    val isOnNakup by seznamViewModel.errorMsg.collectAsState()
    val itemEdit by seznamViewModel.edit.collectAsState()
    val currentNakup = nakupViewModel.currentNakup.collectAsState().value ?: return
    val USE_NEW_ROW = true

    val groupedFlow = remember(
        currentNakup.id,
        currentNakup.sortKategorie,
        currentNakup.sortPolozky
    ) {
        seznamViewModel.seznamMapFlow(currentNakup)
    }
    val grouped by groupedFlow.collectAsState(initial = emptyMap())

    val allCategories = KindOptionEnum.entries

    val expandedCategories = remember(currentNakup) {
        val initialMap: MutableMap<String, Boolean> = mutableMapOf()

        if (currentNakup.categoryExpansionState.isNotBlank()) {
            val parsed = currentNakup.categoryExpansionState
                .toCategoryExpansionMap()
                .toMutableMap()

            // Doplň případně nové kategorie (např. po aktualizaci aplikace)
            allCategories.forEach { category ->
                if (!parsed.containsKey(category.name)) {
                    parsed[category.name] = true
                }
            }

            initialMap.putAll(parsed)

        } else {
            allCategories.forEach { category ->
                initialMap[category.name] = true
            }
        }

        mutableStateMapOf<String, Boolean>().apply { putAll(initialMap) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SeznamTopBar(
                //onHistoryClick = { /*navController.navigate("seznamhistorie")*/ },
                onPridatNakup = { nakupDialog = true },
                menuExpanded = nastaveniDialog,
                onMenuExpandedChange = { nastaveniDialog = it },
                onSortClicked = { sortDialog = true },
                onViewTypeClicked = { viewTypeDialog = true },
            )
        },
    ) { paddingValues ->

        // 7) Vykreslení
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 4.dp
                ),
        ) {

            grouped.forEach { (kategorie, polozkyVKategorii) ->

                item(key = "header_$kategorie") {
                    KategorieHeader(
                        kategorie = kategorie,
                        polozkyVKategoriiSize = polozkyVKategorii.size,
                        currentNakup = currentNakup,
                        expandedCategories = expandedCategories,
                        onToggleExpanded = { kat, stav ->
                            currentNakup.let {
                                nakupViewModel.updateCategoryExpansionState(
                                    it.id,
                                    expandedCategories.toMap()
                                )
                            }
                        }
                    )
                }

                if (expandedCategories[kategorie] == true) {
                    items(
                        polozkyVKategorii,
                        key = { it.id },
                        contentType = { "row" }
                    ) { item ->
                        PolozkaRow2(
                            item,
                            currentNakup,
                            { isChecked -> seznamViewModel.updatePolozku(item.copy(checked = isChecked)) },
                            { seznamViewModel.setEditItem(item) },
                            { seznamViewModel.smazatPolozku(item) },
                            {
                                skladDialog = true
                                selectedItem = item.nazev
                            }
                        )
                    }
                }
            }
        }

        // Dialog pro přidání nakupu
        if (nakupDialog) {
            val today = getCurrentDate()
            NakupAlert(
                onAdd = { nakupViewModel.vlozitNakup(today) },
                onDismiss = { nakupDialog = false }
            )
        }

        if (seznamViewModel.showAddDialog) {
            AddItemDialog(
                onDismiss = {
                    seznamViewModel.clearErrorMsg()
                    seznamViewModel.onDialogClose()
                },
                onConfirm = { nazev, kategorie, mnozstvi ->
                    val currentId = nakupViewModel.currentNakup.value?.id ?: 1
                    seznamViewModel.pridavaniRucne(nazev, kategorie.name, mnozstvi, currentId)
                },
                option = true,
                onNavigate = { navController.navigate("seznampolozky") },
                isEdit = false,
                isOnNakup = isOnNakup,
            )
        }

        if (itemEdit != null) {
            AddItemDialog(
                onDismiss = {
                    seznamViewModel.setEditItem(null)
                    seznamViewModel.clearErrorMsg()
                },
                onConfirm = { nazev, kategorie, mnozstvi ->
                    val updatedSeznam = itemEdit!!.copy(
                        nazev = nazev,
                        kategorie = kategorie.name,
                        quantity = mnozstvi
                    )
                    seznamViewModel.updatePolozku(updatedSeznam)
                },
                option = true,
                onNavigate = { navController.navigate("seznampolozky") },
                isEdit = true,
                isOnNakup = isOnNakup,
                nazev = itemEdit!!.nazev,
                kategorie = KindOptionEnum.valueOf(itemEdit!!.kategorie),
                mnozstvi = itemEdit!!.quantity
            )
        }

        // Dialog pro přidání do vybraného skladu
        if (skladDialog) {
            SkladDialog(
                item = selectedItem,
                skladList = skladList,
                onDismiss = {
                    skladDialog = false
                    selectedItem = ""
                },
                onConfirm = { skladId ->
                    skladDialog = false
                    val encodedNazev = Uri.encode(selectedItem)
                    navController.navigate("pridatPotravinu/$skladId?nazev=$encodedNazev")
                    selectedItem = ""
                }
            )
        }

        if (sortDialog) {
            SortDialog(
                sortDialogVisible = { sortDialog = false },
                nakupSeznam = true,
                nameChange2 = {
                    nakupViewModel.nastavSortPolozky(
                        currentNakup.id,
                        SortOption.NAME
                    )
                },
                quantityChange = {
                    nakupViewModel.nastavSortPolozky(
                        currentNakup.id,
                        SortOption.QUANTITY
                    )
                },

                alphabetChange = {
                    nakupViewModel.nastavSortKategorie(
                        currentNakup.id,
                        SortCategoryOption.ALPHABETICAL
                    )
                },
                defaultChange = {
                    nakupViewModel.nastavSortKategorie(
                        currentNakup.id,
                        SortCategoryOption.DEFAULT
                    )
                },
                countChange = {
                    nakupViewModel.nastavSortKategorie(
                        currentNakup.id,
                        SortCategoryOption.COUNT
                    )
                },

                potraviny2 = currentNakup.sortPolozky,
                kategorie2 = currentNakup.sortKategorie
            )
        }

        if (viewTypeDialog) {
            ViewTypeDialog(
                viewTypeDialogVisible = { viewTypeDialog = false },
                nakupSeznam = true,
                yellowChange = {
                    nakupViewModel.nastavViewType(
                        currentNakup.id,
                        ViewTypeNakup.YELLOW
                    )
                },
                redChange = {
                    nakupViewModel.nastavViewType(
                        currentNakup.id,
                        ViewTypeNakup.ORANGE
                    )
                },
                blueChange = {
                    nakupViewModel.nastavViewType(
                        currentNakup.id,
                        ViewTypeNakup.BLUE
                    )
                },
                localViewType2 = currentNakup.viewType
            )
        }
    }
}


fun getCurrentDate(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return "Nákup ze dne ${currentDateTime.format(formatter)}"
}

@Composable
fun PolozkaRow(
    item: SeznamEntity,
    currentNakup: NakupEntity,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDoSkladu: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clickable { onCheckedChange(!item.checked) }
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {                     // barvíme jen když je to potřeba
                    item.checked -> Color(0xFF4CAF50)
                    else          -> currentNakup.viewType.colors.y
                }
            )
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        Checkbox(
            checked = item.checked,
            onCheckedChange = onCheckedChange,
            interactionSource = remember { MutableInteractionSource() }, // bez ripple
        )
        Icon(
            painter = painterResource(R.drawable.lednicenakup),
            contentDescription = null,
            tint = if (item.checked) Color.Black else Color.Gray,
            modifier = Modifier
                .size(30.dp)
                .clickable(
                    enabled = item.checked,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDoSkladu() }
        )
        Text(
            text = "${item.nazev} (x${item.quantity})",
            modifier = Modifier.weight(1f),
            textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
        )
        Row {                 // Edit + Delete bez Buttonů
            Icon(
                Icons.Default.Edit,
                contentDescription = "edit",
                Modifier
                    .size(24.dp)
                    .clickable { onEdit() }
            )
            Icon(
                Icons.Default.Delete,
                contentDescription = "delete",
                Modifier
                    .size(24.dp)
                    .clickable { onDelete() }
            )
        }
    }
}

@Composable
fun KategorieHeader(
    kategorie: String,
    polozkyVKategoriiSize: Int,
    currentNakup: NakupEntity,
    expandedCategories: MutableMap<String, Boolean>,
    onToggleExpanded: (String, Boolean) -> Unit
) {
    val isExpanded = expandedCategories[kategorie] ?: true

    // ① - Box dostane drawBehind ⇒ nakreslíme linku AŽ po vykreslení obsahu
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val newState = !isExpanded
                expandedCategories[kategorie] = newState
                onToggleExpanded(kategorie, newState)
            }
            .drawBehind {
                // černá čára úplně dole pod headerem
                val stroke = 1.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height - stroke / 2),
                    end   = Offset(size.width, size.height - stroke / 2),
                    strokeWidth = stroke
                )
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)   // trocha vzduchu
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(KindOptionEnum.valueOf(kategorie).stringRes)} " +
                        "($polozkyVKategoriiSize)",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isExpanded)
                    Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Sbalit" else "Rozbalit",
                modifier = Modifier
                    .size(30.dp)
            )
        }
    }
}


@Composable
fun PolozkaRow2(
    item: SeznamEntity,
    currentNakup: NakupEntity,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDoSkladu: () -> Unit
) {
    val noRipple = remember { MutableInteractionSource() }

    // ---- barvy pruhu --------------------------------------------------------
    val stripeColor = if (item.checked)            // ✓ = zelený
        Color(0xFF4CAF50)
    else                                           // ✗ = žlutý (vezmu žlutou, kterou už v appce máš)
        currentNakup.viewType.colors.y
    val stripeWidth = 6.dp                         // šířka proužku
    // ------------------------------------------------------------------------

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .drawWithCache {
                val stripePx = stripeWidth.toPx()

                // celé se vygeneruje JEN při kompozici; při scrollu už se jen vykresluje
                onDrawBehind {
                    // 1) pruh
                    drawRect(
                        color = stripeColor,
                        size  = Size(stripePx, size.height)
                    )
                }
            }
            .clickable(indication = null, interactionSource = noRipple) {
                onCheckedChange(!item.checked)
            }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.checked,
            onCheckedChange = onCheckedChange,
            interactionSource = noRipple
        )

        Icon(
            painterResource(R.drawable.lednicenakup),
            contentDescription = null,
            tint = if (item.checked) Color.Black else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    enabled = item.checked,
                    indication = null,
                    interactionSource = noRipple
                ) { onDoSkladu() }
        )

        Text(
            "${item.nazev} (x${item.quantity})",
            Modifier
                .weight(1f)
                .padding(start = 8.dp),
            textDecoration = if (item.checked) TextDecoration.LineThrough else null
        )

        Icon(
            Icons.Default.Edit,
            contentDescription = "edit",
            modifier = Modifier
                .size(20.dp)
                .clickable(indication = null, interactionSource = noRipple) { onEdit() }
        )
        Icon(
            Icons.Default.Delete,
            contentDescription = "delete",
            modifier = Modifier
                .size(20.dp)
                .clickable(indication = null, interactionSource = noRipple) { onDelete() }
        )
    }
}








