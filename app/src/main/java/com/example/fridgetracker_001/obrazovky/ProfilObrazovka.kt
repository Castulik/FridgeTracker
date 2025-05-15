package com.example.fridgetracker_001.obrazovky

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.notifikace.ExpirationCheckWorker
import com.example.fridgetracker_001.ui.theme.cardGradient12
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
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
@Composable
fun ProfilObrazovka(navController: NavController) {
    val activity = LocalActivity.current
    val context = LocalContext.current

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
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    val cameraGranted = cameraPermission.status.isGranted
    val notifGranted = notificationPermission?.status?.isGranted == true

    Scaffold(containerColor = Color.Transparent) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 4.dp
                )
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .clip(MaterialTheme.shapes.small)
                        .border(2.dp, Color.Black, MaterialTheme.shapes.small)
                        .background(gra)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = stringResource(R.string.o_aplikaci), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.o_aplikaci_popis), style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.planovane_funkce_nadpis), style = MaterialTheme.typography.bodyMedium)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(stringResource(R.string.f_statistiky), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_vice_ikonek), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_sdileni), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_skenovani), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_historie), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_vlastni_ikonka), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_ai_prompt), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_navod), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.f_prohlizeni_db), style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.vyvoj_aktualne), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .clip(MaterialTheme.shapes.small)
                        .border(2.dp, Color.Black, MaterialTheme.shapes.small)
                        .background(arg)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.vyber_lokalizaci))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { switchAppLocale("cs", activity) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF104588))) {
                                Text(stringResource(R.string.czech_btn), color = Color.White)
                            }
                            Button(onClick = { switchAppLocale("en", activity) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF104588))) {
                                Text(stringResource(R.string.english_btn), color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .clip(MaterialTheme.shapes.small)
                        .border(2.dp, Color.Black, MaterialTheme.shapes.small)
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
                        Text(stringResource(R.string.pristup_opravneni))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.kamera))
                                Spacer(Modifier.width(8.dp))
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
                            notificationPermission?.let { permissionState ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(stringResource(R.string.notifikace))
                                    Spacer(Modifier.width(8.dp))
                                    Switch(
                                        checked = notifGranted,
                                        onCheckedChange = {
                                            permissionState.launchPermissionRequest()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .clip(MaterialTheme.shapes.small)
                        .border(2.dp, Color.Black, MaterialTheme.shapes.small)
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.1f to cardGradient12,
                                    0.4f to cardGradient22,
                                )
                            )
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val annotatedLinkString = buildAnnotatedString {
                        append(stringResource(R.string.kredity_ikony) + " ")
                        pushStringAnnotation(tag = "URL", annotation = "https://icons8.com")
                        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                            append("icons8.com")
                        }
                        pop()
                    }
                    Text(
                        text = annotatedLinkString,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable {
                            annotatedLinkString
                                .getStringAnnotations(tag = "URL", start = 0, end = annotatedLinkString.length)
                                .firstOrNull()?.let { annotation ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                    context.startActivity(intent)
                                }
                        },
                    )
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
        val request = OneTimeWorkRequestBuilder<ExpirationCheckWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "ManualTestExpirationWorker",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }) {
        Text(stringResource(R.string.spustit_kontrolu))
    }
}
