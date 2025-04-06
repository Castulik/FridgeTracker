package com.example.fridgetracker_001.obrazovky

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.mojeUI.BottomBarHistorie
import com.example.fridgetracker_001.mojeUI.SeznamTopBar
import com.example.fridgetracker_001.mojeUI.SkladItem
import com.example.fridgetracker_001.ui.theme.buttonPodtvrdit
import com.example.fridgetracker_001.viewmodel.NakupViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel
import com.example.fridgetracker_001.viewmodel.SkladViewModel

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SeznamHistorieObrazovka(
    seznamViewModel: SeznamViewModel,
    skladViewModel: SkladViewModel,
    navController: NavController,
    nakupViewModel: NakupViewModel
) {
    val nakupyList by nakupViewModel.nakupyList.collectAsState()
    val currentNakup by nakupViewModel.currentNakup.collectAsState()

    // Seřazené od nejnovějšího (dle updatedAt)
    val sortedList = remember(nakupyList) {
        nakupyList.sortedByDescending { it.nazev }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SeznamTopBar (
                type = true,
                onCancel = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomBarHistorie(
                copyToNew = {},
                delete = {},
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(sortedList) { nakup ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background( if (nakup == currentNakup) Color.Blue else Color.Gray)
                        .clickable {
                            nakupViewModel.setAsCurrent(nakup.id)
                            navController.popBackStack()
                        }
                ) {
                    Text(
                        text = nakup.nazev,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SeznamHistorieObrazovka2(
    navController: NavController,
    nakupViewModel: NakupViewModel
) {
    val nakupyList by nakupViewModel.nakupyList.collectAsState()
    val currentNakup by nakupViewModel.currentNakup.collectAsState()

    // Seřazení dle data (od nejnovějšího) - parse z nazvu
    val sortedList = remember(nakupyList) {
        nakupyList.sortedByDescending {
            parseNazevAsDate(it.nazev)
        }
    }

    // Stáhnu tam i nějaký text z TextFieldu, abych mohl vložit "nový"
    var novyNakupText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SeznamTopBar (
                type = true,
                onCancel = { navController.popBackStack() }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(8.dp)
        ) {
            // Řádek s TextFieldem a tlačítkem "Přidat"
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.TextField(
                        value = novyNakupText,
                        onValueChange = { novyNakupText = it },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (novyNakupText.isNotBlank()) {
                                nakupViewModel.vlozitNakup(novyNakupText)
                                novyNakupText = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Přidat")
                    }
                }
            }

            // Výpis seznamu
            items(sortedList) { nakup ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            // Nastavíme tento nakup jako "current"
                            nakupViewModel.setAsCurrent(nakup.id)
                        }
                ) {
                    // Zvýrazníme current
                    val isCurrent = (nakup.id == currentNakup?.id)
                    Text(
                        text = if (isCurrent) ">> ${nakup.nazev} <<" else nakup.nazev,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { nakupViewModel.smazatNakup(nakup) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Smazat")
                    }
                }
            }
        }
    }
}

/**
 * Jen pomocná funkce, co zkusí `nazev` přetavit na datum.
 */
fun parseNazevAsDate(nazev: String): Long {
    return try {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val date = sdf.parse(nazev)
        date?.time ?: 0L
    } catch (e: Exception) {
        // Když to nevyjde, vrátíme 0 => šoupne se to nakonec
        0L
    }
}
