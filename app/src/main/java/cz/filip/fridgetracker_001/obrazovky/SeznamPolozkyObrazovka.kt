package cz.filip.fridgetracker_001.obrazovky

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cz.filip.fridgetracker_001.R
import cz.filip.fridgetracker_001.data.KindOptionEnum
import cz.filip.fridgetracker_001.mojeUI.AddItemDialog
import cz.filip.fridgetracker_001.mojeUI.BottomBarPolozky
import cz.filip.fridgetracker_001.mojeUI.SeznamTopBar
import cz.filip.fridgetracker_001.mojeUI.SmazatAlert
import cz.filip.fridgetracker_001.ui.theme.cardGradient22
import cz.filip.fridgetracker_001.viewmodel.NakupViewModel
import cz.filip.fridgetracker_001.viewmodel.PolozkyViewModel
import cz.filip.fridgetracker_001.viewmodel.SeznamViewModel
import cz.filip.fridgetracker_001.viewmodel.SkladViewModel

@OptIn(ExperimentalLayoutApi::class)
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

    val allCategories = KindOptionEnum.entries

    val polozkyList by polozkyViewModel.polozkyFlow.collectAsState()
    var vyber by remember { mutableStateOf(KindOptionEnum.FRUIT_VEG) } // nebo jiný default

    val polozkyVyber = polozkyList.filter { it.kategorie == vyber.name }

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

        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = 4.dp
            )
        ) {

            KategorieGrid(
                allCategories = allCategories,
                vyber = vyber,
                onVyberChange = { vyber = it }
            )

            // 3) Zobraz katalog
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 5.dp,
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                // Samotné položky v dané kategorii
                items(polozkyVyber) { polozka ->

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
                    polozkyViewModel.pridatPolozkaDoKatalogu(newItemNazev, kategorie.name)
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
                    val updatedPolozka = itemEdit!!.copy(nazev = newNazev, kategorie = kategorie.name)
                    polozkyViewModel.updatePolozka(updatedPolozka)
                },
                isEdit = true,
                isDelete = true,
                isOnNakup = isOnNakup,
                onDelete = { smazatAlert = true },
                nazev = itemEdit!!.nazev,
                kategorie = KindOptionEnum.valueOf(itemEdit!!.kategorie)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KategorieGrid(
    allCategories: List<KindOptionEnum>,
    vyber: KindOptionEnum,
    onVyberChange: (KindOptionEnum) -> Unit
) {

    val visibleCategories = allCategories.filter { it != KindOptionEnum.UNKNOWN }

    Box (
        modifier = Modifier
            .padding(2.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(cardGradient22)
            .border(2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
    ){
        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.Top
        ) {
            visibleCategories.forEach { category ->

                val scale by animateFloatAsState(
                    targetValue = if (vyber == category) 1.05f else 1f,
                    animationSpec = tween(150), label = ""
                )

                Button(
                    onClick = { onVyberChange(category) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vyber == category)
                            Color(0xFF66C268)
                        else
                            Color(0xFF104588)
                    ),
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 10.dp,
                        disabledElevation = 0.dp
                    ),
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                ) {
                    Text(
                        text = stringResource(category.stringRes),
                        fontSize = 13.sp,
                        maxLines = 1,
                        color = Color.White
                    )
                }
            }
        }
    }
}



