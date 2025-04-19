package com.example.fridgetracker_001.obrazovky

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.DEFAULT_CATEGORY_STATE
import com.example.fridgetracker_001.data.IconRegistry
import com.example.fridgetracker_001.data.KindOptionEnum
import com.example.fridgetracker_001.data.SortCategoryOption
import com.example.fridgetracker_001.data.SortOption
import com.example.fridgetracker_001.data.ViewTypeNakup
import com.example.fridgetracker_001.data.entities.NakupEntity
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.mojeUI.AddItemDialog
import com.example.fridgetracker_001.mojeUI.MujTextField
import com.example.fridgetracker_001.mojeUI.NakupAlert
import com.example.fridgetracker_001.mojeUI.NavigationIcon
import com.example.fridgetracker_001.mojeUI.SeznamTopBar
import com.example.fridgetracker_001.mojeUI.SkladDialog
import com.example.fridgetracker_001.mojeUI.SortDialog
import com.example.fridgetracker_001.mojeUI.ViewTypeDialog
import com.example.fridgetracker_001.ui.theme.cardGradient1
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.cardPozadi
import com.example.fridgetracker_001.ui.theme.inversePrimaryLightMediumContrast
import com.example.fridgetracker_001.ui.theme.onPrimaryLightMediumContrast
import com.example.fridgetracker_001.ui.theme.primaryLight
import com.example.fridgetracker_001.ui.theme.primaryLightMediumContrast
import com.example.fridgetracker_001.ui.theme.topmenu12
import com.example.fridgetracker_001.viewmodel.NakupViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel
import com.example.fridgetracker_001.viewmodel.SkladViewModel
import java.time.LocalDate
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

    // Vezmeme si "všechny" položky, co existují
    val allSeznamItems by seznamViewModel.seznamyFlow.collectAsState(initial = emptyList())

    // Vezmeme si "aktuální" nákup z NakupViewModel
    val currentNakup by nakupViewModel.currentNakup.collectAsState()

    if (currentNakup == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        return
    } else {
        // Vyfiltrujeme jen ty položky, které patří k currentNakup (podle .nakupId)
        val listForCurrentNakup = remember(allSeznamItems, currentNakup) {
            currentNakup?.let { nakup ->
                allSeznamItems.filter { it.nakupId == nakup.id }
            } ?: emptyList()
        }

        val sortedList = currentNakup?.sortPolozky?.sortFunction?.invoke(listForCurrentNakup)
            ?: listForCurrentNakup
        val grouped = sortedList.groupBy { it.kategorie }
        val sortedGrouped = currentNakup?.sortKategorie?.sortFunction?.invoke(grouped) ?: grouped

        val allCategories = KindOptionEnum.entries

        val expandedCategories = remember(currentNakup) {
            val initialMap: MutableMap<String, Boolean> = mutableMapOf()

            if (currentNakup!!.categoryExpansionState.isNotBlank()) {
                val parsed = currentNakup!!.categoryExpansionState
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

        val vseOdskrtnuto by remember(listForCurrentNakup) {
            mutableStateOf(listForCurrentNakup.isNotEmpty() && listForCurrentNakup.all { it.checked })
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
                    )
            ) {

                sortedGrouped.forEach { (kategorie, polozkyVKategorii) ->

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(currentNakup?.viewType!!.colors.x)
                                .clickable { // Přepneme stav pro tuto kategorii
                                    val newState = !(expandedCategories[kategorie] ?: true)
                                    expandedCategories[kategorie] = newState

                                    currentNakup?.let { it ->
                                        val newMap = expandedCategories.toMap()
                                        nakupViewModel.updateCategoryExpansionState(
                                            it.id,
                                            newMap
                                        )
                                    }
                                }
                                .animateContentSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp),
                                    text = "${stringResource(KindOptionEnum.valueOf(kategorie).stringRes)} (${polozkyVKategorii.size})",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                IconButton(
                                    onClick = {
                                        val newState = !(expandedCategories[kategorie] ?: true)
                                        expandedCategories[kategorie] = newState

                                        currentNakup?.let { it ->
                                            val newMap = expandedCategories.toMap()
                                            nakupViewModel.updateCategoryExpansionState(
                                                it.id,
                                                newMap
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (expandedCategories[kategorie] == true)
                                            Icons.Default.KeyboardArrowUp
                                        else
                                            Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (expandedCategories[kategorie] == true)
                                            "Sbalit" else "Rozbalit",
                                    )
                                }
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = expandedCategories[kategorie] == true,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                polozkyVKategorii.forEach { item ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 2.dp,
                                                start = 4.dp,
                                                end = 4.dp,
                                                bottom = 2.dp
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (item.checked) Color(0xFF4CAF50) else currentNakup?.viewType!!.colors.y)
                                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                    ) {
                                        Checkbox(
                                            checked = item.checked,
                                            onCheckedChange = { isChecked ->
                                                val updatedItem = item.copy(checked = isChecked)
                                                seznamViewModel.updatePolozku(updatedItem)
                                            }
                                        )

                                        IconButton(
                                            onClick = {
                                                skladDialog = true
                                                selectedItem = item.nazev
                                            },
                                            enabled = item.checked
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.lednicenakup),
                                                contentDescription = null,
                                                tint = if (item.checked) Color.Black else Color.Gray
                                            )
                                        }

                                        Text(
                                            text = "${item.nazev} (x${item.quantity})",
                                            modifier = Modifier.weight(1f),
                                            textDecoration = if (item.checked) TextDecoration.LineThrough
                                            else TextDecoration.None
                                        )

                                        IconButton(onClick = {
                                            seznamViewModel.setEditItem(item)
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "edit")
                                        }

                                        IconButton(onClick = {
                                            seznamViewModel.smazatPolozku(item)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
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
                        currentNakup?.id?.let {
                            nakupViewModel.nastavSortPolozky(
                                it,
                                SortOption.NAME
                            )
                        }
                    },
                    quantityChange = {
                        currentNakup?.id?.let {
                            nakupViewModel.nastavSortPolozky(
                                it,
                                SortOption.QUANTITY
                            )
                        }
                    },

                    alphabetChange = {
                        currentNakup?.id?.let {
                            nakupViewModel.nastavSortKategorie(
                                it,
                                SortCategoryOption.ALPHABETICAL
                            )
                        }
                    },
                    defaultChange = {
                        currentNakup?.id?.let {
                            nakupViewModel.nastavSortKategorie(
                                it,
                                SortCategoryOption.DEFAULT
                            )
                        }
                    },
                    countChange = {
                        currentNakup?.id?.let {
                            nakupViewModel.nastavSortKategorie(
                                it,
                                SortCategoryOption.COUNT
                            )
                        }
                    },

                    potraviny2 = currentNakup?.sortPolozky,
                    kategorie2 = currentNakup?.sortKategorie
                )
            }

            if (viewTypeDialog) {
                ViewTypeDialog(
                    viewTypeDialogVisible = { viewTypeDialog = false },
                    nakupSeznam = true,
                    yellowChange = {
                        currentNakup?.id?.let {
                            nakupViewModel.nastavViewType(
                                it,
                                ViewTypeNakup.YELLOW
                            )
                        }
                    },
                    redChange = {
                        currentNakup?.id?.let {
                            nakupViewModel.nastavViewType(
                                it,
                                ViewTypeNakup.ORANGE
                            )
                        }
                    },
                    blueChange = {
                        currentNakup?.id?.let {
                            nakupViewModel.nastavViewType(
                                it,
                                ViewTypeNakup.BLUE
                            )
                        }
                    },
                    localViewType2 = currentNakup?.viewType
                )
            }
        }
    }
}


fun getCurrentDate(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return "Nákup ze dne ${currentDateTime.format(formatter)}"
}

