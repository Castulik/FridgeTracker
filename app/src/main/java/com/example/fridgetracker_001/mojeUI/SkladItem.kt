package com.example.fridgetracker_001.mojeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.ui.theme.cardGradient1
import com.example.fridgetracker_001.ui.theme.cardGradient12
import com.example.fridgetracker_001.ui.theme.cardGradient2
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.cardGradient3
import com.example.fridgetracker_001.ui.theme.onPrimaryContainerLight

@Composable
fun SkladItem(
    sklad: SkladEntity,
    onNavigate: (Int) -> Unit,
    nastaveniSkladu: (Int) -> Unit
) {
    val fontFamily = FontFamily(
        Font(R.font.rosarivo_regular, FontWeight.Normal),
        Font(R.font.rosarivo_italic, FontWeight.Light)
    )

    val gradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to cardGradient1,
            0.4f to cardGradient2,
            0.85f to cardGradient3
        )
    )

    val gradient2 = Brush.verticalGradient(
        colorStops = arrayOf(
            0.6f to cardGradient12,
            0.9f to cardGradient22,
        )
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onNavigate(sklad.id) }
            .height(170.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(20.dp)),
    ) {
        Box(
            modifier = Modifier
                .background(gradient2)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // ===================== LEVÁ ČÁST S OBRÁZKEM =====================
                when {
                    // 1) preferované = true a iconPath != null => zobraz vyfocenou fotku
                    sklad.preferovane && !sklad.iconPath.isNullOrEmpty() -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(sklad.iconPath)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Vyfocená fotka",
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .fillMaxHeight()
                                .aspectRatio(0.8f)
                                .border(1.dp, Color.Black, RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // 2) Jinak (preferované = false, nebo iconPath = null) => zobraz default ikonu
                    else -> {
                        Image(
                            painter = painterResource(id = sklad.iconResourceId),
                            contentDescription = "Ikona skladu",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .fillMaxHeight()
                                .aspectRatio(0.8f)
                                .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                        )
                    }
                }

                // Pravá polovina s textem a ikonami
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.6f)
                        .padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = sklad.nazev,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        thickness = 1.dp,
                        color = Color.Black
                    )

                    // Ikony s vertikálním děličem
                    Row(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onNavigate(sklad.id) },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.plus),
                                contentDescription = "plus",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(40.dp)
                            )
                        }

                        VerticalDivider(
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .padding(vertical = 5.dp)
                        )

                        IconButton(
                            onClick = { nastaveniSkladu(sklad.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.settings),
                                contentDescription = "Settings",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(40.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}