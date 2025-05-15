package cz.filip.fridgetracker_001.mojeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import cz.filip.fridgetracker_001.R
import cz.filip.fridgetracker_001.data.KindOptionEnum
import cz.filip.fridgetracker_001.ui.theme.backSearchItem
import cz.filip.fridgetracker_001.ui.theme.bilaback
import cz.filip.fridgetracker_001.ui.theme.potravinaback
import cz.filip.fridgetracker_001.ui.theme.potravinafront

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
fun MyButton() {
    var text by remember { mutableStateOf("Klikni mě") }

    Button(onClick = { text = "Kliknuto!" }) {
        Text(text)
    }
}

@Composable
fun PotravinaItemGrid2(
    onOptionSelected: (KindOptionEnum) -> Unit = {},
    onNavigate: () -> Unit = {},
    selectedOption: KindOptionEnum = KindOptionEnum.MEAT_FISH,
) {

    val visibleOptions = remember { KindOptionEnum.entries.filter { it != KindOptionEnum.UNKNOWN } }

    val selectedIcons by remember(selectedOption) {
        derivedStateOf { selectedOption.icons }
    }

    Scaffold(
        topBar = {
            TopBarPotravinaForm(
                title = "hh"
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(visibleOptions) { option ->
                    val optionSelected  = option == selectedOption
                    Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .border(
                                width = 2.dp,
                                color = if (optionSelected) Color.Black else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onOptionSelected(option)
                            }
                            // 1) Celá buňka má daný poměr stran (šířka : výška = 1 : 0.8)
                            .fillMaxWidth()
                            .aspectRatio(0.7f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 2) Horní část pro obrázek: zabere 75 % výšky buňky
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = option.imageRes),
                                contentDescription = stringResource(option.stringRes),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                            )
                            if (optionSelected) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color(0x804CAF50)) // zelený overlay s průhledností
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                        // 3) Spodní část pro text: zabere 25 % výšky buňky
                        Box(
                            modifier = Modifier
                                .weight(0.5f, fill = true)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(option.stringRes),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = Color.Black,
                                minLines = 2,                       // dovolíme až 2 řádky
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            if (optionSelected) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color(0x804CAF50)) // zelený overlay s průhledností
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }
                }

                if (selectedIcons.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        HorizontalDivider(
                            thickness = 3.dp,
                            modifier = Modifier
                                .padding(vertical = 4.dp),
                            color = Color.Black
                        )
                    }

                    items(selectedIcons) { icon ->
                        Image(
                            painter = painterResource(id = icon.resId),
                            contentDescription = icon.name,
                            modifier = Modifier
                                .clickable {
                                    onNavigate()
                                }
                                .padding(4.dp)
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDatePickerWithIcon() {

        PotravinaItemGrid2()
}
