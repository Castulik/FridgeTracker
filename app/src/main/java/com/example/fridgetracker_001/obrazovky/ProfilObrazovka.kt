package com.example.fridgetracker_001.obrazovky

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfilObrazovka(
    navController: NavController
) {
    Scaffold(

        containerColor = Color.Transparent,
    ) { paddingValues ->
        Box( //Box mi reprezentuje cely screan
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 4.dp
                ) //content bude presne licovat se spodnim menu
        ) {

        }
    }
}