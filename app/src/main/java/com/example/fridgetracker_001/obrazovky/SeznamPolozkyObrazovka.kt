package com.example.fridgetracker_001.obrazovky

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.mojeUI.BottomBarPolozky
import com.example.fridgetracker_001.mojeUI.SeznamTopBar
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

    var polozkyDialog by remember { mutableStateOf(false) }

    val currentNakup by nakupViewModel.currentNakup.collectAsState()

    val allSeznamItems by seznamViewModel.seznamyFlow.collectAsState(initial = emptyList())

    // Vyfiltrujeme jen ty položky, které patří k currentNakup (podle .nakupId)
    val listForCurrentNakup = remember(allSeznamItems, currentNakup) {
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
                addKatalog = { polozkyDialog = true },
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
                    Text(
                        text = kategorie,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp)
                    )
                }

                // Samotné položky v dané kategorii
                items(polozkyVKategorii) { polozka ->

                    val isInCurrentNakup = listForCurrentNakup.any {
                        it.nazev == polozka.nazev && it.kategorie == polozka.kategorie
                    }
                    val rowColor = if (isInCurrentNakup) Color(0xFFE5FFCC) else Color.White

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(rowColor)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        val existingItem = listForCurrentNakup.find {
                            it.nazev == polozka.nazev && it.kategorie == polozka.kategorie
                        }
                        Text(
                            text = "${polozka.nazev} (x${existingItem?.quantity})",
                            modifier = Modifier.weight(1f)
                        )
                        // Plus tlačítko -> přidat do "SeznamEntity"
                        IconButton(onClick = {
                            // Zjistíme aktivní nakupId
                            val currentId = nakupViewModel.currentNakup.value?.id ?: 1
                            seznamViewModel.pridatNeboZvysitPolozku(nazev = polozka.nazev, kategorie = polozka.kategorie, nakupId = currentId)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Přidat do nákupu"
                            )
                        }
                        IconButton(onClick = {
                            val currentId = nakupViewModel.currentNakup.value?.id ?: 1
                            if (existingItem != null) {
                                seznamViewModel.odebratNeboSnizitPolozku(nazev = polozka.nazev, kategorie = polozka.kategorie, nakupId = currentId)
                            }
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.minus),
                                contentDescription = "Odebrat",
                                modifier = Modifier.size(25.dp), tint = Color.Black
                            )
                        }

                        IconButton(onClick = {
                            // Smazat z katalogu
                            polozkyViewModel.smazatPolozkaZKatalogu(polozka)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete from katalog")
                        }
                    }
                }
            }
        }

        if (polozkyDialog) {
            AddItemDialog(
                onDismiss = { polozkyDialog = false },
                onConfirm = { newItemNazev, kategorie ->
                    polozkyViewModel.pridatPolozkaDoKatalogu(newItemNazev, kategorie)
                    polozkyDialog = false
                }
            )
        }
    }
}