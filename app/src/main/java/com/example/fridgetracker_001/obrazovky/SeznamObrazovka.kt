package com.example.fridgetracker_001.obrazovky

import android.net.Uri
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.DEFAULT_CATEGORY_STATE
import com.example.fridgetracker_001.data.entities.NakupEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.mojeUI.MujTextField
import com.example.fridgetracker_001.mojeUI.NavigationIcon
import com.example.fridgetracker_001.mojeUI.SeznamTopBar
import com.example.fridgetracker_001.ui.theme.cardGradient1
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.cardPozadi
import com.example.fridgetracker_001.ui.theme.inversePrimaryLightMediumContrast
import com.example.fridgetracker_001.ui.theme.onPrimaryLightMediumContrast
import com.example.fridgetracker_001.ui.theme.primaryLightMediumContrast
import com.example.fridgetracker_001.viewmodel.NakupViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel
import com.example.fridgetracker_001.viewmodel.SkladViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/*
@Composable
fun SeznamObrazovka(
    seznamViewModel: SeznamViewModel,
    skladViewModel: SkladViewModel,
    navController: NavController,
) {
    val list by seznamViewModel.seznamyFlow.collectAsState(initial = emptyList())
    var skladDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }
    val skladList by skladViewModel.skladList.collectAsState()

    val allCategories = listOf(
        "Mražené", "Trvanlivé", "Ovoce a Zelenina", "Mléčné výrobky", "Maso a Ryby",
        "Pečivo", "Vejce", "Obiloviny a luštěniny",
        "Uzeniny a lahůdky", "Nápoje", "Hotová jídla", "Ostatní"
    )
    // Rozdělení potravin dle druhu
    val mapByCategory = list.groupBy { it.kategorie }
    val seznamEntity = list.firstOrNull()

    // Případně jen: val allCategories = mapByCategory.keys.sorted()

    // 5) Z "theSeznam.categoryExpansionState" uděláme Map<String, Boolean>
    val expandedCategories = remember(seznamEntity) {
        val initialMap: MutableMap<String, Boolean> = mutableMapOf()

        if (seznamEntity != null && seznamEntity.categoryExpansionState.isNotBlank()) {
            val parsed = seznamEntity.categoryExpansionState.toCategoryExpansionMap().toMutableMap()

            // Zajistíme, že pro každou kategorii existuje záznam (default = true)
            allCategories.forEach { category ->
                if (!initialMap.containsKey(category)) {
                    initialMap[category] = true
                }
            }
            initialMap.putAll(parsed)
            // Výsledek dáme do mutableStateMapOf, abychom mohli z UI přepínat
        } else {
            allCategories.forEach { category -> initialMap[category] = true }
        }
        mutableStateMapOf<String, Boolean>().apply { putAll(initialMap) }
    }

    // 6) Podobně můžeme načíst např. "viewType", "sortKategorie" apod. z theSeznam
    //    a případně je nechat upravit. (Teď pro ukázku vynechám.)

    // 7) V composable vykreslíme LazyColumn, uvnitř:
    //    - pro každou kategorii "Header"
    //    - pokud expandedCategories[cat] == true -> vykreslíme seznam položek

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SeznamTopBar(
                onHistoryClick = { /*TODO*/ }
            )
        }
    ) { paddingValues ->
        Box( //Box mi reprezentuje cely screan
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 0.dp
                ) //content bude presne licovat se spodnim menu
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                items(list) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(if (item.checked) Color.Green else Color.LightGray)
                    ) {

                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = { isChecked ->
                                // vytvoříme nové "item" se změnou checked
                                val updatedItem = item.copy(checked = isChecked)
                                // zavoláme viewModel, aby uložil změnu do DB
                                seznamViewModel.updatePolozku(updatedItem)
                            }
                        )

                        IconButton(
                            onClick = {
                                skladDialog = true
                                selectedItem = item.nazev
                            },
                            enabled = item.checked // false = nepůjde kliknout
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = if (item.checked) Color.Black else Color.Gray
                            )
                        }


                        Text(
                            text = item.nazev,
                            modifier = Modifier.weight(1f),
                            textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
                        )

                        IconButton(onClick = { seznamViewModel.smazatPolozku(item) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }

            if (seznamViewModel.showAddDialog) {
                AddItemDialog(
                    onDismiss = { seznamViewModel.onDialogClose() },
                    onConfirm = { newItemNazev, kategorie ->
                        val newItem = SeznamEntity(nazev = newItemNazev, kategorie = kategorie)
                        seznamViewModel.pridatPolozku(newItem)
                        seznamViewModel.onDialogClose()
                    }
                )
            }

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
        }
    }
}

 */

@Composable
fun SeznamObrazovka2(
    seznamViewModel: SeznamViewModel,
    skladViewModel: SkladViewModel,
    navController: NavController,
    nakupViewModel: NakupViewModel
) {
    var skladDialog by remember { mutableStateOf(false) }
    var nakupDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }
    val skladList by skladViewModel.skladList.collectAsState()

    // Vezmeme si "všechny" položky, co existují
    val allSeznamItems by seznamViewModel.seznamyFlow.collectAsState(initial = emptyList())

    // Vezmeme si "aktuální" nákup z NakupViewModel
    val currentNakup by nakupViewModel.currentNakup.collectAsState()

    // Vyfiltrujeme jen ty položky, které patří k currentNakup (podle .nakupId)
    val listForCurrentNakup = remember(allSeznamItems, currentNakup) {
        currentNakup?.let { nakup ->
            allSeznamItems.filter { it.nakupId == nakup.id }
        } ?: emptyList()
    }

    val polozkyByCategory = remember(listForCurrentNakup) {
        listForCurrentNakup.groupBy { it.kategorie }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SeznamTopBar(
                onHistoryClick = { navController.navigate("seznamhistorie") },
                onPridatNakup = { nakupDialog = true },
                onNastaveni = { },
            )
        }
    ) { paddingValues ->

        // 7) Vykreslení
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            polozkyByCategory.forEach { (kategorie, polozkyVKategorii) ->
                // Heading kategorie
                item {
                    Text(
                        text = kategorie,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp)
                    )
                }
            // Pro každou reálně existující kategorii
                items(polozkyVKategorii) { item ->
                // Získáme seznam položek v dané kategorii
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(if (item.checked) Color.Green else Color.White)
                    ) {

                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = { isChecked ->
                                // vytvoříme nové "item" se změnou checked
                                val updatedItem = item.copy(checked = isChecked)
                                // zavoláme viewModel, aby uložil změnu do DB
                                seznamViewModel.updatePolozku(updatedItem)
                            }
                        )

                        // Přidání do skladu (jen pokud je odškrtnuto)
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
                            seznamViewModel.smazatPolozku(item)
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }
        }

        // Dialog pro přidání nové položky
        if (nakupDialog) {
            val today = getCurrentDate()
            AlertDialog(
                onDismissRequest = { nakupDialog = false },
                title = { Text("Novy nakup") },
                text = {
                    Text("pridej novy nakup")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val newNakup = NakupEntity(nazev = today)
                            nakupViewModel.vlozitNakup(newNakup)
                            nakupDialog = false
                        }
                    ) {
                        Text("Přidat")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {nakupDialog = false}
                    ) {
                        Text("Zrušit")
                    }
                }
            )
        }

        if (seznamViewModel.showAddDialog) {
            AddItemDialog(
                onDismiss = { seznamViewModel.onDialogClose() },
                onConfirm = { newItemNazev, kategorie ->
                    val currentId = nakupViewModel.currentNakup.value?.id ?: 1
                    seznamViewModel.pridatNeboZvysitPolozku(nazev = newItemNazev, kategorie = kategorie, nakupId = currentId)
                    seznamViewModel.onDialogClose()
                },
                option = true,
                onNavigate = { navController.navigate("seznampolozky") }
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
    }
}


/*
AddItemDialog(
                onDismiss = { seznamViewModel.onDialogClose() },
                onConfirm = { newItemNazev, kategorie ->
                    val newItem = SeznamEntity(nazev = newItemNazev, kategorie = kategorie)
                    seznamViewModel.pridatPolozku(newItem)
                    seznamViewModel.onDialogClose()
                }
            )
 */


@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    onNavigate: () -> Unit = {},
    option: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    val isError by remember { mutableStateOf(false) }

    val options2 = listOf(
        "Mražené", "Trvanlivé", "Ovoce a Zelenina", "Mléčné výrobky",
        "Maso a Ryby", "Pečivo", "Vejce", "Obiloviny a luštěniny",
        "Uzeniny a lahůdky", "Nápoje", "Hotová jídla", "Ostatní"
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Přidat položku") },
        text = {
            Column {

                MujTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Název potraviny",
                    isError = isError,
                    errorMessage = "",
                    maxLength = 30,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardPozadi,
                        unfocusedContainerColor = cardPozadi,
                        disabledContainerColor = cardPozadi,
                        errorContainerColor = cardPozadi,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorTextColor = Color.Red
                    )
                )

                if(option) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {onNavigate();onDismiss()},
                        // Tady si nastavíte design tlačítka
                        shape = RoundedCornerShape(12.dp),   // zaoblené rohy
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4), // tyrkysová
                            contentColor = Color.White          // barva textu
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle, // Ikona ruky (můžete změnit)
                                contentDescription = "Click Icon",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(text = "Přidat pomocí katalogu", fontSize = 17.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Vyber kategorii:")
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(options2) { category ->
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .border(
                                    width = if (selectedCategory == category) 2.dp else 1.dp,
                                    color = if (selectedCategory == category) Color.Black else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedCategory = category
                                    showError = false
                                }
                                .padding(8.dp)
                                .aspectRatio(1.5f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pro přidání potraviny musíte vybrat kategorii!",
                        color = Color.Red,
                        fontSize = 15.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedCategory != null) {
                        onConfirm(text, selectedCategory!!)
                    } else {
                        showError = true
                    }
                }
            ) {
                Text("Přidat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Zrušit")
            }
        }
    )
}



@Composable
fun SkladDialog(
    item: String,        // typ podle toho, co máte
    skladList: List<SkladEntity>,     // typ skladů
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit    // vracíme skladId
) {
    var selectedSklad by remember { mutableStateOf<SkladEntity?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Přidat položku: $item")
        },
        text = {
            Column {
                Text("Vyber sklad ze seznamu:")
                Spacer(modifier = Modifier.height(8.dp))
                // Jednoduchý výčet skladů s RadioButton
                skladList.forEach { sklad ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSklad = sklad }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (sklad == selectedSklad),
                            onClick = { selectedSklad = sklad }
                        )
                        Text(
                            text = sklad.nazev,    // nebo sklad.name, podle vašeho modelu
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // voláme onConfirm s ID vybraného skladu
                    selectedSklad?.let { onConfirm(it.id) }
                },
                enabled = (selectedSklad != null)  // tlačítko je aktivní jen pokud je vybrán sklad
            ) {
                Text("Přidat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Zrušit")
            }
        }
    )
}

fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return currentDate.format(formatter)
}
