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

    // Seřazené od nejnovějšího (dle updatedAt)
    val sortedList = remember(nakupyList) {
        nakupyList.sortedByDescending { it.updatedAt }
    }

    // Definujeme formát pro datum a čas
    val dateFormat = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    }

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
                .padding(paddingValues)
        ) {
            items(sortedList) { nakup ->
                // Z `updatedAt` vyrobíme Date a formátujeme
                val formattedDate = remember(nakup.updatedAt) {
                    dateFormat.format(Date(nakup.updatedAt))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background(Color.Gray)
                        .clickable {
                            nakupViewModel.setAsCurrent(nakup.id)
                            navController.popBackStack()
                        }
                ) {
                    Text(
                        text = "${nakup.nazev} (aktualizováno: $formattedDate)",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
