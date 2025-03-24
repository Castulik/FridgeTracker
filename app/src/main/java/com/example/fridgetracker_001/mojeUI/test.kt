package com.example.fridgetracker_001.mojeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.ui.theme.backSearchItem
import com.example.fridgetracker_001.ui.theme.bilaback
import com.example.fridgetracker_001.ui.theme.potravinaback
import com.example.fridgetracker_001.ui.theme.potravinafront

@Composable
fun SearchItem(){

    val daysLeft  = 10L
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable {

            }
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backSearchItem)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
    ) {
        Image(
            painter = painterResource(id = R.drawable.food_hranolky),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bilaback)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
        )

        Text(
            text = "potravina.nazev",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        VerticalDivider(modifier = Modifier.width(1.dp).padding(vertical = 4.dp), color = Color.Black)

        Text(
            text = "skladName",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        VerticalDivider(modifier = Modifier.width(1.dp).padding(vertical = 4.dp), color = Color.Black)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(
                    if (daysLeft != null)
                        getBackgroundColorForDaysLeft(daysLeft)
                    else
                        Color.Gray // Když se nepovede datum parse-ovat
                )
                .weight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatDaysLeft(daysLeft),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun PotravinaItem(){
    val daysLeft  = 10L

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(potravinaback)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Obrázek
        Image(
            painter = painterResource(id = R.drawable.food_hranolky),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bilaback)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
        )

        // Obsah uprostřed
        Box(
            modifier = Modifier
                .height(50.dp)
                .padding(5.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(potravinafront),
        ) {
            Row(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxSize(),
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.7f), // Zajistí roztažení na střed
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "potravina.nazev",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun PotravinaItemGrid(
    nazev: String = "Steak",
    daysLeft: Int = 56,
    iconRes: Int = R.drawable.food_hranolky // Tvůj steak obrázek
) {
    // Speciální tvar bubliny, která je zakulacená s výjimkou levého dolního rohu
    val dayBubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomEnd = 16.dp,
        bottomStart = 0.dp
    )

    // Celá karta
    Box(
        modifier = Modifier
            // V gridu se šířka přizpůsobí 1/4 sloupce díky LazyVerticalGrid(columns = Fixed(4))
            //.fillMaxWidth()
            // Pokud chceš poměr stran např. 3:4:
            //.aspectRatio(0.7f)
            .height(104.dp)
            .width(82.dp)
            .padding(3.dp)
            .clip(RoundedCornerShape(12.dp))            // zaoblené rohy
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .background(Color(0xFFE0E0E0)),              // světle šedé pozadí
    ) {
        // Sloupec s obrázkem nahoře a textem dole
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1) Horní část = obrázek
            Box(
                modifier = Modifier
                    .weight(1f)   // Zbytek prostoru
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = nazev,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // 2) Dělící linka nebo menší barevný pruh
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.Black
            )

            // 3) Spodní část = název potraviny
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(potravinafront),   // světle růžový/hnědý pruh
                contentAlignment = Alignment.Center
            ) {
                Text(text = nazev)
            }
        }

        // 4) Bublina s počtem dní do expirace
        Box(
            modifier = Modifier
                // Zarovnání do pravého horního rohu
                .align(Alignment.TopEnd)
                // Posunout trochu ven, aby "vykukovala"
                .offset(x = (8).dp, y = (-8).dp)
                .clip(dayBubbleShape)
                .background(Color(0xFF66BB6A)) // zelená
                .border(1.dp, Color.Black, shape = dayBubbleShape)
                .padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = daysLeft.toString(), color = Color.White)
        }
    }
}

@Composable
fun PotravinaItemGrid2(
    nazev: String = "Steak",
    daysLeft: Int = 56,
    iconRes: Int = R.drawable.food_hranolky
) {

}

@Preview(showBackground = true)
@Composable
fun PreviewDatePickerWithIcon() {

        PotravinaItemGrid2()
}
