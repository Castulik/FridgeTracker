package com.example.fridgetracker_001.obrazovky

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.notifikace.ExpirationCheckWorker
import com.example.fridgetracker_001.notifikace.NotificationUtils
import com.example.fridgetracker_001.ui.theme.cardGradient12
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.cardPozadi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted

fun switchAppLocale(tag: String, activity: Activity?) {
    val locales = if (tag.isBlank()) {
        LocaleListCompat.getEmptyLocaleList()
    } else {
        LocaleListCompat.forLanguageTags(tag)
    }
    AppCompatDelegate.setApplicationLocales(locales)

    if (Build.VERSION.SDK_INT < 33 && activity != null) {
        activity.recreate()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ProfilObrazovka(navController: NavController) {
    val activity = LocalActivity.current
    val gra = Brush.verticalGradient(
        colorStops = arrayOf(
            0.6f to cardGradient12,
            0.9f to cardGradient22,
        )
    )
    val arg = Brush.verticalGradient(
        colorStops = arrayOf(
            0.1f to cardGradient22,
            0.4f to cardGradient12,
        )
    )

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    val cameraGranted = cameraPermission.status is PermissionStatus.Granted
    val notifGranted = notificationPermission.status is PermissionStatus.Granted

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(gra)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("O aplikaci")
                    Text("Novinky které jsou v plánu: statistiky nákupů, sdílené sklady napříč rodinou, skenování pomocí čárových kódů napojit na API")
                }
            }

            // 2
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(arg)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Vyber Lokalizaci")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { switchAppLocale("cs", activity) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF104588)
                        ),) {
                            Text(stringResource(R.string.czech_btn), color = Color.White)
                        }
                        Button(
                            onClick = { switchAppLocale("en", activity) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF104588)
                            ),
                            ) {
                            Text(stringResource(R.string.english_btn), color = Color.White)
                        }
                    }
                }
            }

            // 3
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(gra)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Nahlédni do své databáze čárových kódů ? NEEEEEE")
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.1f to cardGradient22,
                                0.4f to cardGradient12,
                            )
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Přístup k oprávněním")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Kamera")
                            Spacer(Modifier.width(8.dp))
                            val context = LocalContext.current
                            Switch(
                                checked = cameraGranted,
                                onCheckedChange = {
                                    if (!cameraGranted) {
                                        cameraPermission.launchPermissionRequest()
                                    } else {
                                        openAppSettings(context)
                                    }
                                }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Notifikace")
                            Spacer(Modifier.width(8.dp))
                            Switch(
                                checked = notifGranted,
                                onCheckedChange = { notificationPermission.launchPermissionRequest() }
                            )
                        }
                    }
                }
            }

            // 5
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(gra)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Testovací obrazovka", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    SpustitNotifikaciHnedButton()
                }
            }
        }
    }
}


fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Composable
fun SpustitNotifikaciHnedButton() {
    val context = LocalContext.current

    Button(onClick = {
        val request = OneTimeWorkRequestBuilder<ExpirationCheckWorker>()
            .build() // bez zpoždění = hned

        WorkManager.getInstance(context).enqueueUniqueWork(
            "ManualTestExpirationWorker",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }) {
        Text("Spustit kontrolu expirací")
    }
}

