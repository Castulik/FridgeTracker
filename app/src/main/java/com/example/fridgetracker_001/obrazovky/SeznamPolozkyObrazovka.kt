package com.example.fridgetracker_001.obrazovky

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.mojeUI.AddItemDialog
import com.example.fridgetracker_001.mojeUI.BottomBarPolozky
import com.example.fridgetracker_001.mojeUI.SeznamTopBar
import com.example.fridgetracker_001.mojeUI.SmazatAlert
import com.example.fridgetracker_001.ui.theme.buttonPodtvrdit
import com.example.fridgetracker_001.viewmodel.NakupViewModel
import com.example.fridgetracker_001.viewmodel.PolozkyViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel
import com.example.fridgetracker_001.viewmodel.SkladViewModel

@Composable
fun SeznamPolozkyObrazovka(
    seznamViewModel: SeznamViewModel,
    skladViewModel: SkladViewModel,
    navController: NavController,
    nakupViewModel: NakupViewModel,
    polozkyViewModel: PolozkyViewModel,
) {
    val isOnNakup by polozkyViewModel.errorMsg.collectAsState()
    val itemEdit by polozkyViewModel.edit.collectAsState()
    val currentNakup by nakupViewModel.currentNakup.collectAsState()

    val allSeznamItems by seznamViewModel.seznamyFlow.collectAsState(initial = emptyList())
    // Vyfiltrujeme jen ty položky, které patří k currentNakup (podle .nakupId)
    val aktualniNakupItems = remember(allSeznamItems, currentNakup) {
        currentNakup?.let { nakup ->
            allSeznamItems.filter { it.nakupId == nakup.id }
        } ?: emptyList()
    }

    val polozkyList by polozkyViewModel.polozkyFlow.collectAsState()
    // 2) Seskupíme podle kategorie
    val polozkyByCategory = remember(polozkyList) {
        polozkyList.groupBy { it.kategorie }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SeznamTopBar (
                type = false,
                onCancel = { navController.navigate("seznam") }
            )
        },
        bottomBar = {
            BottomBarPolozky(
                addKatalog = { polozkyViewModel.onDialogOpen() },
                onDone = { navController.navigate("seznam") }
            )
        }
    ) { paddingValues ->

        // 3) Zobraz katalog
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pro každou kategorii vykresli "heading" + seznam položek
            polozkyByCategory.forEach { (kategorie, polozkyVKategorii) ->
                // Heading kategorie
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFFF6BE3D))
                            .clickable { // Přepneme stav pro tuto kategorii

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
                                text = "${kategorie?.let { stringResource(it) } ?: ""} (${polozkyVKategorii.size})",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            IconButton(
                                onClick = {

                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.KeyboardArrowDown,
                                    contentDescription =
                                        "Sbalit",
                                )
                            }
                        }
                    }
                }

                // Samotné položky v dané kategorii
                items(polozkyVKategorii) { polozka ->

                    val isInCurrentNakup = aktualniNakupItems.any {
                        it.nazev == polozka.nazev && it.kategorie == polozka.kategorie
                    }
                    val rowColor = if (isInCurrentNakup) Color(0xFF66C268) else Color(0xFFF5F5DC)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, start = 4.dp, end = 4.dp, bottom = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(rowColor)
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    ) {
                        val existingItem = aktualniNakupItems.find {
                            it.nazev == polozka.nazev && it.kategorie == polozka.kategorie
                        }
                        Text(
                            text = if (existingItem?.quantity == null) "${polozka.nazev} (-)" else "${polozka.nazev} (x${existingItem.quantity})",
                            modifier = Modifier.weight(1f).padding(start = 5.dp)
                        )
                        // Plus tlačítko -> přidat do "SeznamEntity"
                        IconButton(onClick = {
                            val currentId = nakupViewModel.currentNakup.value?.id ?: 1
                            seznamViewModel.pridatZKatalogu(polozka, currentId)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Přidat do nákupu"
                            )
                        }
                        IconButton(onClick = {
                            val currentId = nakupViewModel.currentNakup.value?.id ?: 1
                            seznamViewModel.odebratZKatalogu(polozka, currentId)
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.minus),
                                contentDescription = "Odebrat",
                                modifier = Modifier.size(25.dp), tint = Color.Black
                            )
                        }

                        IconButton(onClick = {
                            polozkyViewModel.setEditItem(polozka)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "edit")
                        }
                    }
                }
            }
        }

        if (polozkyViewModel.showAddDialog) {
            AddItemDialog(
                onDismiss = {
                    polozkyViewModel.clearErrorMsg()
                    polozkyViewModel.onDialogClose()
                },
                onConfirm2 = { newItemNazev, kategorie ->
                    polozkyViewModel.pridatPolozkaDoKatalogu(newItemNazev, kategorie)
                },
                isEdit = false,
                isOnNakup = isOnNakup,
            )
        }

        if (itemEdit != null) {

            var smazatAlert by remember { mutableStateOf(false) }
            AddItemDialog(
                onDismiss = {
                    polozkyViewModel.clearErrorMsg()
                    polozkyViewModel.setEditItem(null)
                },
                onConfirm2 = { newNazev, kategorie ->
                    val updatedPolozka = itemEdit!!.copy(nazev = newNazev, kategorie = kategorie)
                    polozkyViewModel.updatePolozka(updatedPolozka)
                },
                isEdit = true,
                isDelete = true,
                isOnNakup = isOnNakup,
                onDelete = { smazatAlert = true },
                nazev = itemEdit!!.nazev,
                kategorie = itemEdit!!.kategorie
            )

            if (smazatAlert) {
                SmazatAlert(
                    onDelete = {
                        polozkyViewModel.smazatPolozkaZKatalogu(itemEdit!!)
                        polozkyViewModel.setEditItem(null)
                        smazatAlert = false
                    },
                    change = {
                        smazatAlert = false
                    }
                )
            }
        }
    }
}