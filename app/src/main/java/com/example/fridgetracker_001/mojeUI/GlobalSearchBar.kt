package com.example.fridgetracker_001.mojeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.ui.theme.CaribbeanCurrent
import com.example.fridgetracker_001.ui.theme.backSearchItem
import com.example.fridgetracker_001.ui.theme.bilaback
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.cardGradient3
import com.example.fridgetracker_001.ui.theme.cardPozadi
import com.example.fridgetracker_001.viewmodel.PotravinaViewModel
import com.example.fridgetracker_001.viewmodel.SkladViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onScanClick: () -> Unit,
    filteredItems: List<PotravinaEntity>,
    getSkladName: (Int) -> String,
    getDaysLeft: (PotravinaEntity) -> Long?,
    onItemClick: (PotravinaEntity) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    // Barvy pro SearchBar
    val customColors = colors(
        containerColor = Color(0xFF5FA1E0), // pozadí hlavního search baru
        dividerColor = Color.Black
    )
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {
                    // Když uživatel stiskne "Enter" (search), schováme klávesnici
                    focusManager.clearFocus()
                },
                expanded = active,
                onExpandedChange = onActiveChange,
                enabled = true,
                placeholder = { Text("Zadej název, kategorii nebo čárový kod", fontSize = 15.sp) },
                leadingIcon = {
                    if (!active) {
                        // Normální režim: zobrazíme ikonu lupy
                        Icon(Icons.Default.Search, contentDescription = "Search icon")
                    } else {
                        // Rozbalený režim: zobrazíme šipku zpět
                        IconButton(onClick = {
                            // Nastaví active na false => "zavře" se
                            onActiveChange(false)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Zpět"
                            )
                        }
                    }
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Křížek na smazání textu
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onClearQuery() }) {
                                Icon(Icons.Default.Close, contentDescription = "Vymazat")
                            }
                        }
                        // Ikona pro spuštění skeneru
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.barcode)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { onScanClick() },
                            colorFilter = ColorFilter.tint(Color.Black) // Přebarvení na bílou
                        )
                    }
                },
                interactionSource = null,
                colors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Black,
                    disabledLeadingIconColor = Color.Black,
                    focusedTrailingIconColor = Color.Black,
                    unfocusedTrailingIconColor = Color.Black,
                    disabledTrailingIconColor = Color.Black,
                    focusedPlaceholderColor = Color.Black,
                    unfocusedPlaceholderColor = Color.Black,
                    disabledPlaceholderColor = Color.Black,
                )
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        shape = if (active) RoundedCornerShape(2.dp) else RoundedCornerShape(12.dp),
        colors = customColors,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        windowInsets = SearchBarDefaults.windowInsets,
        content = {
            // Výsledky hledání (nebo návrhy) – zobrazujeme jen to, co přijde přes filteredItems
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(3.dp)) {
                items(filteredItems) { potravina ->
                    val skladNazev = getSkladName(potravina.skladId)
                    val daysLeft = getDaysLeft(potravina)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                onItemClick(potravina)
                            }
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backSearchItem)
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = potravina.potravinaIconaId),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bilaback)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        )

                        Text(
                            text = potravina.nazev,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        VerticalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .padding(vertical = 4.dp),
                            color = Color.Black
                        )

                        Text(
                            text = skladNazev,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        VerticalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .padding(vertical = 4.dp),
                            color = Color.Black
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    if (daysLeft != null)
                                        getBackgroundColorForDaysLeft(daysLeft)
                                    else
                                        Color.Gray // Když se nepovede datum parse-ovat
                                )
                                .weight(0.6f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatDaysLeft(daysLeft),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchBar2(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onScanClick: () -> Unit,
    filteredItems: List<PotravinaEntity>,
    getSkladName: (Int) -> String,
    getDaysLeft: (PotravinaEntity) -> Long?,
    onItemClick: (PotravinaEntity) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    // Barvy pro SearchBar
    val customColors = SearchBarDefaults.colors(
        containerColor = cardPozadi,  // Vaše barva pozadí
        dividerColor   = Color.Black
    )

    // Zaoblení pro sbalený/rozbalený stav
    val searchBarShape = if (active) RoundedCornerShape(24.dp) else RoundedCornerShape(12.dp)

    // Box pro border a clip
    Box(
        modifier = Modifier
            // Menší padding, ať nám SearchBar nevyplní tolik místa
            .padding(top=50.dp)
            // Snížíme celkovou výšku SearchBaru
            .height(60.dp)
            // Rámeček kopírující tvar
            .border(1.dp, Color.Black, searchBarShape)
            // Oříznutí tvaru tak, aby border nebyl „useknutý“
            .clip(searchBarShape)
            // Stejná barva jako SearchBar, aby to splynulo
            .background(cardPozadi)
            .fillMaxWidth()
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        // Když uživatel stiskne "Enter" (search), schováme klávesnici
                        focusManager.clearFocus()
                    },
                    expanded = active,
                    onExpandedChange = onActiveChange,
                    enabled = true,
                    placeholder = {
                        Text("název, druh, nebo čárový kod", fontSize = 15.sp)
                    },
                    leadingIcon = {
                        if (!active) {
                            // Sbalený režim: lupa
                            Icon(Icons.Default.Search, contentDescription = "Search icon")
                        } else {
                            // Rozbalený režim: šipka zpět
                            IconButton(onClick = { onActiveChange(false) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                            }
                        }
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Křížek na smazání textu
                            if (query.isNotEmpty()) {
                                IconButton(onClick = onClearQuery) {
                                    Icon(Icons.Default.Close, contentDescription = "Vymazat")
                                }
                            }
                            // Ikona pro spuštění skeneru (zmenšíme na 24dp)
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(R.drawable.barcode)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onScanClick() },
                                colorFilter = ColorFilter.tint(Color.Black)
                            )
                        }
                    },
                    interactionSource = null,
                    colors = SearchBarDefaults.inputFieldColors(
                        focusedTextColor = Color.Black,
                        disabledTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLeadingIconColor = Color.Black,
                        unfocusedLeadingIconColor = Color.Black,
                        disabledLeadingIconColor = Color.Black,
                        focusedTrailingIconColor = Color.Black,
                        unfocusedTrailingIconColor = Color.Black,
                        disabledTrailingIconColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        unfocusedPlaceholderColor = Color.Black,
                        disabledPlaceholderColor = Color.Black,
                    )
                )
            },
            expanded = active,
            onExpandedChange = onActiveChange,
            shape = searchBarShape,           // Tvar SearchBaru
            modifier = Modifier.fillMaxWidth(),
            colors = customColors,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
            content = {
                // Výsledky hledání (nebo návrhy) – zobrazujeme jen to, co přijde přes filteredItems
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                ) {
                    items(filteredItems) { potravina ->
                        val skladNazev = getSkladName(potravina.skladId)
                        val daysLeft = getDaysLeft(potravina)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable { onItemClick(potravina) }
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(backSearchItem)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(id = potravina.potravinaIconaId),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bilaback)
                                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                            )

                            Text(
                                text = potravina.nazev,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            VerticalDivider(
                                modifier = Modifier
                                    .width(1.dp)
                                    .padding(vertical = 4.dp),
                                color = Color.Black
                            )

                            Text(
                                text = skladNazev,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            VerticalDivider(
                                modifier = Modifier
                                    .width(1.dp)
                                    .padding(vertical = 4.dp),
                                color = Color.Black
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (daysLeft != null)
                                            getBackgroundColorForDaysLeft(daysLeft)
                                        else
                                            Color.Gray // Když se nepovede datum parse-ovat
                                    )
                                    .weight(0.6f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = formatDaysLeft(daysLeft),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            },
        )
    }
}



