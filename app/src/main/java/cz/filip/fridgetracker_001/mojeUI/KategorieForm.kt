package cz.filip.fridgetracker_001.mojeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.filip.fridgetracker_001.data.FoodIcon
import cz.filip.fridgetracker_001.data.KindOptionEnum
import cz.filip.fridgetracker_001.ui.theme.buttoncolor

@Composable
fun KategorieForm(
    onCancel: () -> Unit = {},
    onOptionSelected: (KindOptionEnum) -> Unit = {},
    onNavigate: (FoodIcon) -> Unit = {},
    selectedOption: KindOptionEnum = KindOptionEnum.UNKNOWN,
    selectedIcon: FoodIcon,
    title: String,
    snackbarHostState: SnackbarHostState,
){
    Scaffold(
        topBar = {
            TopBarPotravinaForm(
                onCancel = {
                    onCancel()
                },
                title = title
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(4.dp)
        ){
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(KindOptionEnum.entries.filter { it != KindOptionEnum.UNKNOWN }) { option ->
                    val optionSelected = option == selectedOption

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 2.dp,
                                color = if (optionSelected) Color.Black else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onOptionSelected(option) }
                            .background(buttoncolor),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 🔹 Obrázek má fixní poměr stran – tedy stejnou výšku u všech
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f) // např. čtvercový obrázek
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = option.imageRes),
                                contentDescription = stringResource(option.stringRes),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (optionSelected) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color(0x66FFEB3B)) // zelený overlay
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }

                        Text(
                            text = stringResource(option.stringRes),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = Color.Black,
                            minLines = 2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    HorizontalDivider(
                        thickness = 3.dp,
                        modifier = Modifier
                            .padding(vertical = 4.dp),
                        color = Color.Black
                    )
                }

                val selectedIcons = selectedOption.icons
                items(selectedIcons) { icon ->

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(icon) }
                            .border(
                                width = if (icon == selectedIcon) 3.dp else 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = icon.resId),
                            contentDescription = icon.name,
                            modifier = Modifier
                                .matchParentSize() // obrázek přes celý box
                                .background(Color.LightGray)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        if (icon == selectedIcon) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color(0x66FFEB3B)) // poloprůhledná žlutá
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)    // zarovnat nahoru (uprostřed)
                    .padding(bottom = 8.dp)         // třeba mírný odskok
            )
        }
    }
}