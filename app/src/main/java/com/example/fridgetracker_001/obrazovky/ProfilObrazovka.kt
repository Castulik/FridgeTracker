package com.example.fridgetracker_001.obrazovky

import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.example.fridgetracker_001.R

/**
 *  tag = "en"  → vynutí angličtinu
 *  tag = ""    → zruší override → appka zase používá default (= češtinu)
 */
fun switchAppLocale(tag: String, activity: Activity?) {
    val locales = if (tag.isBlank()) {
        LocaleListCompat.getEmptyLocaleList()
    } else {
        LocaleListCompat.forLanguageTags(tag)
    }
    AppCompatDelegate.setApplicationLocales(locales)

    // od Androidu 13 (API 33) už není potřeba restart
    if (android.os.Build.VERSION.SDK_INT < 33 && activity != null) {
        // potlač animace → žádné černé bliknutí
        activity.recreate()
    }                         // refresh UI
}

// ---------- 2) samotná obrazovka ----------

@Composable
fun ProfilObrazovka(navController: NavController) {

    // hoď kamkoli do Composable (třeba do ProfilObrazovka)

    val activity = LocalActivity.current

    Scaffold (
        containerColor = Color.Transparent
    ){ padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Button(
                    onClick = { switchAppLocale("cs", activity) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(stringResource(R.string.czech_btn))
                }

                Button(
                    onClick = { switchAppLocale("", activity) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(stringResource(R.string.english_btn))
                }
            }
        }
    }
}

