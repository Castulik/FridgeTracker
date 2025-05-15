package cz.filip.fridgetracker_001.mojeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cz.filip.fridgetracker_001.R
import cz.filip.fridgetracker_001.data.entities.PotravinaEntity
import cz.filip.fridgetracker_001.obrazovky.ViewType
import cz.filip.fridgetracker_001.ui.theme.bilaback
import cz.filip.fridgetracker_001.ui.theme.cardGradient2
import cz.filip.fridgetracker_001.ui.theme.cardGradient3
import cz.filip.fridgetracker_001.ui.theme.potravinaback
import cz.filip.fridgetracker_001.ui.theme.potravinafront
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun PotravinaItem(
    potravina: PotravinaEntity,
    viewType: ViewType,
    getDaysLeft: (PotravinaEntity) -> Long?,
    smazatPotravina: () -> Unit,
    pridejNaSeznam: () -> Unit,
    editPotravina: () -> Unit,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
    isSelected: Boolean = false
) {
    val daysLeft = getDaysLeft(potravina)
    var deleteAlert by remember { mutableStateOf(false) }

    // Rozhodneme, jakou tloušťku/ barvu okraje použijeme, když je položka vybraná
    val borderModifier = if (isSelected) {
        Modifier.border(3.dp, cardGradient3, RoundedCornerShape(8.dp))
    } else {
        Modifier.border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    }

    // Gesto: tap a long-press
    val modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onTap = { onTap() },
            onLongPress = { onLongPress() }
        )
    }
    val backgroundColor = if (isSelected) {
        cardGradient2
    } else {
        potravinaback
    }

    when (viewType) {
        ViewType.LIST -> {
                Row(
                    modifier = modifier
                        .height(104.dp)
                        .fillMaxWidth()
                        .padding(top = 2.dp, start = 4.dp, end = 4.dp, bottom = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .then(borderModifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Obrázek
                    Image(
                        painter = painterResource(id = potravina.potravinaIkona.resId),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .weight(0.4f)
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                    )

                    // Obsah uprostřed
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .weight(1f) // Zajistí roztažení na střed
                            .background(if (isSelected) Color.Transparent else potravinafront),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxSize(),
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = potravina.nazev,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                text = stringResource(R.string.ostatni_dateStored, potravina.datumPridani),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            // Spodní řádek: množství, hmotnost, daysLeft
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                // 2.1) MNOŽSTVÍ
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (isSelected) Color.Transparent else potravinaback)
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Oddělíme číslo (potravina.mnozstvi) od "ks"
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = potravina.mnozstvi,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Text(
                                            text = stringResource(R.string.ostatni_pcs),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                // 2.2) HMOTNOST
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (isSelected) Color.Transparent else potravinaback)
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Stejný princip - číslo + " g"/"kg"/"ml"
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = potravina.vaha,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Text(
                                            text = " ${potravina.jednotky}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                // 2.3) DAYS LEFT
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(
                                            if (isSelected) {
                                                Color.Transparent
                                            } else {
                                                if (daysLeft != null) getBackgroundColorForDaysLeft(daysLeft)
                                                else Color.Gray
                                            }
                                        )
                                        .weight(0.8f),
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

                    // Tlačítka pro mazání a nastavení
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { editPotravina() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.editmuj),
                                contentDescription = "Nastavení"
                            )
                        }

                        IconButton(
                            onClick = { deleteAlert = true }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.trashmuj),
                                contentDescription = "Smazat"
                            )
                        }

                        if (deleteAlert) {
                            DeleteAlert2(
                                change = { deleteAlert = false },
                                onDelete = { isChecked ->
                                    if (isChecked) {
                                        pridejNaSeznam()
                                        smazatPotravina()
                                    } else
                                    smazatPotravina()
                                }
                            )
                        }
                    }
                }
        }

        ViewType.SMALL_LIST -> {
            Row(
                modifier = modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .padding(top = 2.dp, start = 4.dp, end = 4.dp, bottom = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .then(borderModifier),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Obrázek
                Image(
                    painter = painterResource(id = potravina.potravinaIkona.resId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bilaback)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                )

                // Obsah uprostřed
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.Transparent else potravinafront),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxSize(),
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(0.6f), // Zajistí roztažení na střed
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = potravina.nazev,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // 2) Množství, váha, daysLeft
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            // 2.1) MNOŽSTVÍ
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color.Transparent else potravinaback)
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(
                                        text = potravina.mnozstvi,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = " ks",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }

                            // 2.2) VÁHA
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color.Transparent else potravinaback)
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(
                                        text = potravina.vaha,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = " ${potravina.jednotky}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }

                            // 2.3) DAYS LEFT
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) {
                                            Color.Transparent
                                        } else {
                                            if (daysLeft != null) getBackgroundColorForDaysLeft(daysLeft)
                                            else Color.Gray
                                        }
                                    )
                                    .weight(0.8f),
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
            }
        }
        ViewType.GRID -> {
            // Speciální tvar bubliny
            val dayBubbleShape = RoundedCornerShape(
                topStart = 10.dp,
                topEnd = 10.dp,
                bottomEnd = 10.dp,
                bottomStart = 0.dp
            )

            // Vnější Box, který NEklipuje děti
            Box(
                modifier = modifier
                    // V gridu se šířka přizpůsobí 1/4 sloupce díky LazyVerticalGrid(columns = Fixed(4))
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .padding(5.dp)
            ) {
                // 1) Samotná karta (vnitřní Box, který klipuje svůj obsah)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .then(borderModifier),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Horní část s obrázkem
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Image(
                                painter = painterResource(id = potravina.potravinaIkona.resId),
                                contentDescription = "obrazek potraviny",
                                modifier = Modifier
                                    .size(60.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Oddělující čára
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Black
                        )
                        // Spodní část s názvem
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp)
                                .background(if (isSelected) Color.Transparent else potravinafront),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = potravina.nazev,
                                color = Color.Black,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                // 2) Bublina pro dny do expirace: sibling (sourozenec) s vyšším zIndex
                Box(
                    modifier = Modifier
                        // Vyšší "vrstva" nad Boxem pro kartu
                        .zIndex(1f)
                        // Zarovnat do pravého horního rohu vně (posunout ven)
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = (-5).dp)
                        .clip(dayBubbleShape)
                        .background(
                            if (daysLeft != null)
                                getBackgroundColorForDaysLeft(daysLeft)
                            else
                                Color.Gray // Když se nepovede datum parse-ovat
                        )
                        .border(1.dp, Color.Black, dayBubbleShape)
                        .padding(horizontal = 5.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatDaysLeft(daysLeft),
                        color = Color.Black,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

fun calculateDaysToExpiration(dateString: String): Long? {
    return try {
        // Nastavení formátu podle toho, jak vám to uživatel zadává (dd.MM.yyyy).
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        // Lokální datum vycházející z uživatelského vstupu.
        val expirationDate = LocalDate.parse(dateString, formatter)

        // Dnešní datum.
        val today = LocalDate.now()

        // Výpočet rozdílu v dnech.
        ChronoUnit.DAYS.between(today, expirationDate)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Funkce pro určení barvy
fun getBackgroundColorForDaysLeft(daysLeft: Long): Color {
    return when {
        daysLeft <= 0 -> Color(0xFFAB1E1E)   // rudě červená
        daysLeft in 1..3 -> Color.Red        // červená
        daysLeft in 4..7 -> Color(0xFFFFD922) // tmavě žlutá
        daysLeft in 8..14 -> Color(0xFFFFFF2F) // světle žlutá
        else -> Color(0xFF00C853)           // zelená
    }
}

// Funkce pro formátovaný text
fun formatDaysLeft(days: Long?): String {
    return if (days == null) {
        "?"
    } else
        "$days"
}