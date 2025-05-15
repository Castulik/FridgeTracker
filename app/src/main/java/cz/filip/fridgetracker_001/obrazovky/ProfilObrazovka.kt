package cz.filip.fridgetracker_001.obrazovky

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
import cz.filip.fridgetracker_001.R
import cz.filip.fridgetracker_001.notifikace.ExpirationCheckWorker
import cz.filip.fridgetracker_001.ui.theme.cardGradient12
import cz.filip.fridgetracker_001.ui.theme.cardGradient22
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

    // 🛡️ Bezpečné ošetření POST_NOTIFICATIONS jen pro API 33+
    val notificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        } else null

    val cameraGranted = cameraPermission.status.isGranted
    val notifGranted = notificationPermission?.status?.isGranted == true

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // 1 – O aplikaci
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(gra)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.Start) {

                    // Nadpis
                    Text(
                        text = "O aplikaci",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Popis aplikace
                    Text(
                        text = "FridgeTracker je jednoduchá, ale chytrá aplikace pro správu potravin ve tvé lednici nebo mrazáku. Umožňuje sledovat trvanlivost potravin, vytvářet nákupní seznamy, organizovat sklad a mít přehled o tom, co máš doma.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nadpis plánů
                    Text(
                        text = "Plánuji přidat nové funkce, které zlepší používání aplikace:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Odrážky
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Statistiky nákupů", style = MaterialTheme.typography.bodySmall)
                        Text("• Přidání více iconek potravin", style = MaterialTheme.typography.bodySmall)
                        Text("• Sdílené sklady napříč rodinou", style = MaterialTheme.typography.bodySmall)
                        Text("• Skenování čárových kódů napojené na databázi", style = MaterialTheme.typography.bodySmall)
                        Text("• Přidávání potravin z historického seznamu", style = MaterialTheme.typography.bodySmall)
                        Text("• Možnost vyfotit si vlastní ikonku potraviny", style = MaterialTheme.typography.bodySmall)
                        Text("• AI prompt builder (návrhy přes všechny sklady)", style = MaterialTheme.typography.bodySmall)
                        Text("• Návod na použití aplikace", style = MaterialTheme.typography.bodySmall)
                        Text("• Možnost prohlížet databázi čárových kódů", style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Co se právě vyvíjí
                    Text(
                        text = "Aktuálně pracuji na tom, aby si uživatel mohl vyfotit vlastní potravinu a použít ji jako ikonku.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }




            // 2 – Lokalizace
            Box(
                modifier = Modifier
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF104588)),
                        ) {
                            Text(stringResource(R.string.czech_btn), color = Color.White)
                        }
                        Button(
                            onClick = { switchAppLocale("en", activity) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF104588)),
                        ) {
                            Text(stringResource(R.string.english_btn), color = Color.White)
                        }
                    }
                }
            }

            // 3 – Oprávnění
            Box(
                modifier = Modifier
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
                        // Kamera
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Kamera")
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

                        notificationPermission?.let { permissionState: com.google.accompanist.permissions.PermissionState ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Notifikace")
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

            // 4 – Kredity za ikony
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
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
                    append("Icons by ")

                    pushStringAnnotation(
                        tag = "URL",
                        annotation = "https://icons8.com"
                    )

                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
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

