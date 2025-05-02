package com.example.fridgetracker_001

import android.graphics.BitmapShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fridgetracker_001.mojeUI.CustomBottomBar
import com.example.fridgetracker_001.obrazovky.EditPotravinaObrazovka2
import com.example.fridgetracker_001.obrazovky.EditSkladObrazovka2
import com.example.fridgetracker_001.obrazovky.KategorieObrazovka
import com.example.fridgetracker_001.obrazovky.KategorieObrazovkaEdit
import com.example.fridgetracker_001.viewmodel.SkladViewModel
import com.example.fridgetracker_001.obrazovky.MojePotravinyObrazovka
import com.example.fridgetracker_001.obrazovky.PridatPotravinuObrazovka2
import com.example.fridgetracker_001.obrazovky.PridatSkladObrazovka2
import com.example.fridgetracker_001.obrazovky.ProfilObrazovka
import com.example.fridgetracker_001.obrazovky.ScannerObrazovkaRoute
import com.example.fridgetracker_001.obrazovky.SeznamHistorieObrazovka
import com.example.fridgetracker_001.obrazovky.SeznamObrazovka2
import com.example.fridgetracker_001.obrazovky.SeznamPolozkyObrazovka
import com.example.fridgetracker_001.obrazovky.SkladObrazovka2
import com.example.fridgetracker_001.ui.theme.cardGradient12
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.topmenu111
import com.example.fridgetracker_001.ui.theme.topmenu2
import com.example.fridgetracker_001.viewmodel.CodeViewModel
import com.example.fridgetracker_001.viewmodel.NakupViewModel
import com.example.fridgetracker_001.viewmodel.PolozkyViewModel
import com.example.fridgetracker_001.viewmodel.PotravinaViewModel
import com.example.fridgetracker_001.viewmodel.SeznamViewModel


@Composable
fun MainScaffold(
    skladViewModel: SkladViewModel,
    potravinaViewModel: PotravinaViewModel,
    codeViewModel: CodeViewModel,
    seznamViewModel: SeznamViewModel,
    nakupViewModel: NakupViewModel,
    polozkyViewModel: PolozkyViewModel
) {

    val context = LocalContext.current
    val imageBitmap = ImageBitmap.imageResource(context.resources, R.drawable.textura3)
    val bitmap = imageBitmap.asAndroidBitmap()
    val shaderBrush = remember {
        val shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        ShaderBrush(shader)
    }

    val imageBitmap2 = ImageBitmap.imageResource(context.resources, R.drawable.texturazelena)
    val bitmap2 = imageBitmap2.asAndroidBitmap()
    val shaderBrush2 = remember {
        val shader = BitmapShader(bitmap2, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        ShaderBrush(shader)
    }

    val gradient2 = Brush.verticalGradient(
        colorStops = arrayOf(
            0.1f to topmenu111,
            1f to topmenu2,
        )
    )

    val gradient3 = Brush.verticalGradient(
        colorStops = arrayOf(
            0.6f to cardGradient12,
            0.9f to cardGradient22,
        )
    )

    val controler = rememberNavController()
    val navBackStackEntry by controler.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                when (currentRoute) {
                    "SkladObrazovka/{skladId}" -> drawRect(brush = shaderBrush2, size = size)
                    "pridatPotravinu/{skladId}?nazev={nazev}&barcode={barcode}" -> drawRect(brush = gradient2, size = size)
                    "editPotravinu/{potravinaId}?barcode={barcode}" -> drawRect(brush = gradient2, size = size)
                    "nastaveniskladu/{skladId}" -> drawRect(brush = gradient3, size = size)
                    "skladpridat" -> drawRect(brush = gradient3, size = size)
                    "scanner?returnRoute={returnRoute}&skladId={skladId}" -> drawRect(brush = gradient2, size = size)
                    "KategorieObrazovka/{skladId}/{fromScreen}?nazev={nazev}&barcode={barcode}&kategorie={kategorie}" -> drawRect(brush = gradient2, size = size)
                    "KategorieObrazovkaEdit/{potravinaId}" -> drawRect(brush = gradient2, size = size)
                    else -> drawRect(brush = shaderBrush, size = size)
                }
                // Pak vykreslíme vlastní obsah
                drawContent()
            }
    ) {

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (currentRoute in listOf("seznam", "mojepotraviny/?barcode={barcode}", "profil")) {
                    CustomBottomBar(
                        onItemClick = { item ->
                            if (currentRoute != item.cesta) {
                                controler.navigate(item.cesta) {
                                    popUpTo(controler.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onPlusClick = {
                            when (currentRoute) {
                                "mojepotraviny/?barcode={barcode}" -> {
                                    controler.navigate("skladpridat")
                                }
                                "seznam" -> {
                                    seznamViewModel.onDialogOpen()
                                }
                                "profil" -> {
                                    // 3) Třeba popUp "Není nic k přidání" :-)
                                }
                            }
                        },
                        currentRoute = currentRoute
                    ) // Spodní menu zobrazíme pouze na určitých obrazovkách
                }
            },  // Spodní menu
        ) { paddingValues ->

            NavHost(
                navController = controler,
                startDestination = "mojepotraviny/?barcode={barcode}", // Výchozí obrazovka
                modifier = Modifier
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                composable("seznam") {
                    /*
                    SeznamObrazovka(
                        seznamViewModel = seznamViewModel,
                        skladViewModel = skladViewModel,
                        navController = controler
                    )

                     */

                    SeznamObrazovka2(
                        seznamViewModel = seznamViewModel,
                        skladViewModel = skladViewModel,
                        nakupViewModel = nakupViewModel,
                        navController = controler
                    )
                }

                composable("seznampolozky") {
                    SeznamPolozkyObrazovka(
                        seznamViewModel = seznamViewModel,
                        skladViewModel = skladViewModel,
                        nakupViewModel = nakupViewModel,
                        navController = controler,
                        polozkyViewModel = polozkyViewModel
                    )
                }

                composable("seznamhistorie") {
                    SeznamHistorieObrazovka(
                        seznamViewModel = seznamViewModel,
                        skladViewModel = skladViewModel,
                        nakupViewModel = nakupViewModel,
                        navController = controler
                    )
                }

                composable(
                    route ="mojepotraviny/?barcode={barcode}",
                    arguments = listOf(
                    navArgument("barcode") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true }
                    )
                ) { backStackEntry ->
                    val barcode  = backStackEntry.arguments?.getString("barcode") ?: ""
                    MojePotravinyObrazovka(
                        navController = controler,
                        skladViewModel = skladViewModel,
                        potravinaViewModel = potravinaViewModel,
                        barcode = barcode
                    )
                }

                composable("skladpridat") {
                    PridatSkladObrazovka2(
                        skladViewModel = skladViewModel,
                        navController = controler
                    )
                }

                composable(
                    route = "nastaveniskladu/{skladId}",
                    arguments = listOf(navArgument("skladId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val skladId = backStackEntry.arguments?.getInt("skladId") ?: 0
                    EditSkladObrazovka2(
                        skladViewModel = skladViewModel,
                        navController = controler,
                        skladId = skladId,
                    )
                }

                composable("profil") {
                    ProfilObrazovka(
                        navController = controler
                    )
                }

                composable(
                    route = "SkladObrazovka/{skladId}",
                    arguments = listOf(navArgument("skladId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val skladId = backStackEntry.arguments?.getInt("skladId") ?: 0
                    SkladObrazovka2(
                        skladId = skladId,
                        skladViewModel = skladViewModel,
                        potravinaViewModel = potravinaViewModel,
                        navController = controler,
                        seznamViewModel = seznamViewModel,
                        nakupViewModel = nakupViewModel
                    )
                }

                composable(
                    route = "KategorieObrazovka/{skladId}/{fromScreen}?nazev={nazev}&barcode={barcode}&kategorie={kategorie}",
                    arguments = listOf(
                        navArgument("skladId") { type = NavType.IntType },
                        navArgument("nazev") {
                            type = NavType.StringType
                            defaultValue = ""           // Pokud param nepřijde, použije se ""
                            nullable = true
                        },
                        navArgument("barcode") {
                            type = NavType.StringType
                            defaultValue = ""           // Pokud param nepřijde, použije se ""
                            nullable = true
                        },
                        navArgument("kategorie") {
                            type = NavType.StringType
                            defaultValue = ""           // Pokud param nepřijde, použije se ""
                            nullable = true
                        }
                    )
                ) { backStackEntry ->
                    val skladId = backStackEntry.arguments?.getInt("skladId") ?: 0
                    val fromScreen = backStackEntry.arguments?.getString("fromScreen") ?: "fromForm"
                    val nazev = backStackEntry.arguments?.getString("nazev") ?: ""
                    val barcode  = backStackEntry.arguments?.getString("barcode") ?: ""
                    val kategorie  = backStackEntry.arguments?.getString("kategorie") ?: ""

                    KategorieObrazovka(
                        skladId = skladId,
                        potravinaViewModel = potravinaViewModel,
                        navController = controler,
                        fromScreen = fromScreen,
                        nazevPolozky = nazev,
                        barcodePolozky = barcode,
                        kategoriePolozky = kategorie
                    )
                }

                composable(
                    route = "KategorieObrazovkaEdit/{potravinaId}",
                    arguments = listOf(navArgument("potravinaId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val potravinaId = backStackEntry.arguments?.getInt("potravinaId") ?: 0

                    KategorieObrazovkaEdit(
                        potravinaId = potravinaId,
                        potravinaViewModel = potravinaViewModel,
                        navController = controler
                    )
                }

                composable(
                    route = "pridatPotravinu/{skladId}?nazev={nazev}&barcode={barcode}",
                    arguments = listOf(
                        navArgument("skladId") { type = NavType.IntType },
                        navArgument("nazev") {
                            type = NavType.StringType
                            defaultValue = ""           // Pokud param nepřijde, použije se ""
                            nullable = true
                        },
                        navArgument("barcode") {
                            type = NavType.StringType
                            defaultValue = ""           // Pokud param nepřijde, použije se ""
                            nullable = true
                        }
                    )
                ) { backStackEntry ->
                    val skladId = backStackEntry.arguments?.getInt("skladId") ?: 0
                    val nazev = backStackEntry.arguments?.getString("nazev") ?: ""
                    val barcode  = backStackEntry.arguments?.getString("barcode") ?: ""

                    /*
                    PridatPotravinuObrazovka(
                        skladId = skladId,
                        nazevPolozky = nazev,        // Odesíláme do Composable
                        potravinaViewModel = potravinaViewModel,
                        navController = controler,
                        codeviewmodel = codeViewModel
                    )
                     */

                    PridatPotravinuObrazovka2(
                        skladId = skladId,
                        nazevPolozky = nazev,        // Odesíláme do Composable
                        barcodePolozky = barcode,
                        potravinaViewModel = potravinaViewModel,
                        navController = controler,
                    )
                }


                composable(
                    route = "editPotravinu/{potravinaId}?barcode={barcode}",
                    arguments = listOf(
                        navArgument("potravinaId") { type = NavType.IntType },
                        navArgument("barcode") {
                            type = NavType.StringType
                            defaultValue = ""           // Pokud param nepřijde, použije se ""
                            nullable = true
                        }
                    )
                ) { backStackEntry ->
                    val potravinaId = backStackEntry.arguments?.getInt("potravinaId") ?: 0
                    val barcode  = backStackEntry.arguments?.getString("barcode") ?: ""
                    /*
                    EditPotravinaObrazovka(
                        potravinaId = potravinaId,
                        potravinaviewmodel = potravinaViewModel,
                        navController = controler,
                        codeviewmodel = codeViewModel
                    )
                     */

                    EditPotravinaObrazovka2(
                        potravinaId = potravinaId,
                        barcodePolozky = barcode,
                        potravinaViewModel = potravinaViewModel,
                        navController = controler,
                        seznamViewModel = seznamViewModel,
                        nakupViewModel = nakupViewModel
                    )
                }

                composable(
                    route = "scanner?returnRoute={returnRoute}&skladId={skladId}",
                    arguments = listOf(
                        navArgument("returnRoute") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument("skladId") {
                            type = NavType.IntType
                            defaultValue = 0
                        }
                    )
                ) { backStackEntry ->
                    val returnRoute = backStackEntry.arguments?.getString("returnRoute") ?: ""
                    val skladId = backStackEntry.arguments?.getInt("skladId") ?: 0

                    ScannerObrazovkaRoute(
                        navController = controler, // nebo jakkoli se jmenuje tvůj navController
                        returnRoute = returnRoute,
                        skladId = skladId,
                        codeViewModel = codeViewModel
                    )
                }
            }
        }
    }
}