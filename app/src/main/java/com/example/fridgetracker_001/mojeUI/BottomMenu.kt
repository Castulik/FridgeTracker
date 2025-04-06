package com.example.fridgetracker_001.mojeUI

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.ui.theme.buttonPodtvrdit
import com.example.fridgetracker_001.ui.theme.cardGradient12
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.cardGradient3

data class NavigationItem(
    val cesta: String,  // cestaNavigaceItem
    val icon: NavigationIcon, // Přidání ImageVector pro ikonu
    val text: String
)

sealed class NavigationIcon {
    data class Vector(val icon: ImageVector) : NavigationIcon()
    data class PainterIcon(val painter: Painter) : NavigationIcon()
}

/* ====== Spodní menu pro normální režim (Přidat potravinu) ====== */
@Composable
fun BottomBarAddPotravinu(
    onAddClick: () -> Unit,
    onQrClick: () -> Unit
) {
    // Vytvoříme velké tlačítko přes celou šířku
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(15.dp))
                .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp))
                .background(buttonPodtvrdit),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .clickable { onAddClick() }
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.plus),
                    contentDescription = "Přidat potravinu",
                    modifier = Modifier.size(50.dp)
                )

                Text("Přidat potravinu", fontSize = 15.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider(modifier = Modifier.width(2.dp).height(65.dp), color = Color.Black, thickness = 2.dp)

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onQrClick() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.barcode),
                    contentDescription = "Přidat potravinu barcode",
                    modifier = Modifier.height(50.dp).width(70.dp)
                )

                Text("Přidej skenem", fontSize = 15.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun CustomBottomBar(
    onItemClick: (NavigationItem) -> Unit,
    onPlusClick: () -> Unit,
    currentRoute: String?
) {
    val items = listOf(
        NavigationItem(
            cesta = "seznam",
            icon = NavigationIcon.Vector(Icons.AutoMirrored.Filled.List),
            text = "Seznam"
        ),
        NavigationItem(
            cesta = "mojepotraviny/?barcode={barcode}",
            icon = NavigationIcon.PainterIcon(painterResource(id = R.drawable.kitchen)),
            text = "Sklad"
        ),
        NavigationItem(
            cesta = "profil",
            icon = NavigationIcon.Vector(Icons.Filled.AccountCircle),
            text = "Profil"
        )
    )

    val shape = RoundedCornerShape(24.dp)

    val gradient2 = Brush.verticalGradient(
        colorStops = arrayOf(
            0.6f to cardGradient12,
            0.9f to cardGradient22,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 50.dp)
            .wrapContentSize(align = Alignment.BottomCenter)
            .clip(shape)
            .shadow(elevation = 8.dp, shape = shape)
            .border(1.dp, Color.Black, shape)
            .background(cardGradient22),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Levá část (70 %) s NavigationItemy
            Row(
                modifier = Modifier
                    .weight(0.7f)   // Zabere 70 %
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items.forEach { item ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onItemClick(item)
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (val icon = item.icon) {
                            is NavigationIcon.Vector -> Icon(
                                imageVector = icon.icon,
                                contentDescription = item.text,
                                modifier = Modifier.size(30.dp),
                                tint = if (currentRoute == item.cesta) cardGradient3 else Color.Black
                            )
                            is NavigationIcon.PainterIcon -> Icon(
                                painter = icon.painter,
                                contentDescription = item.text,
                                modifier = Modifier.size(30.dp),
                                tint = if (currentRoute == item.cesta) cardGradient3 else Color.Black
                            )
                        }
                        Text(item.text, fontSize = 13.sp, color = Color.Black)
                    }
                }
            }

            // Prostřední mezera (10 %)
            Spacer(modifier = Modifier.weight(0.2f))

            // Pravý Box (20 %)
            Box(
                modifier = Modifier
                    .weight(0.2f)   // Zabere 20 %
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .shadow(elevation = 16.dp, RoundedCornerShape(8.dp))
                    .clickable { onPlusClick() },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.plus2)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().background(cardGradient22)
                )
            }
        }
    }
}


/* ====== Spodní menu pro multi-select ====== */
@Composable
fun BottomBarMultiSelect(
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    pridejNaSeznam: () -> Unit,
    skladList: List<SkladEntity>,
    onMove: (SkladEntity) -> Unit,
    ai: Boolean,
    onConfirmClick: () -> Unit,
) {

    var moveMenuExpanded by remember { mutableStateOf(false) }
    var deleteAlert by remember { mutableStateOf(false) }

    if(!ai) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(vertical = 4.dp, horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp))
                    .background(Color.LightGray),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Přesunout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { moveMenuExpanded = true }
                ) {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.move),
                        contentDescription = "Přesunout",
                        modifier = Modifier.size(30.dp)
                    )
                    Text("Přesunout", fontSize = 15.sp)

                    // 2) DropdownMenu pro výběr skladu, který se zobrazí, když moveMenuExpanded = true
                    DropdownMenu(
                        expanded = moveMenuExpanded,
                        onDismissRequest = { moveMenuExpanded = false },
                        // Můžeš upravit offset, pokud chceš “drop-up” efekt
                        offset = DpOffset(x = (0).dp, y = (0).dp)
                    ) {
                        // Pro každý sklad v seznamu vykresli položku
                        skladList.forEach { sklad ->
                            DropdownMenuItem(
                                text = { Text(sklad.nazev) },
                                onClick = {
                                    // Po kliknutí se zavolá callback, který provede přesun potravin
                                    onMove(sklad)
                                    moveMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Kopírovat
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCopy() }
                ) {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.copy),
                        contentDescription = "Kopírovat",
                        modifier = Modifier.size(30.dp)
                    )
                    Text("Kopírovat", fontSize = 15.sp)
                }

                // Smazat
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { deleteAlert = true }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Smazat",
                        modifier = Modifier.size(30.dp)
                    )
                    Text("Smazat", fontSize = 15.sp)
                }

                if (deleteAlert) {
                    DeleteAlert2(
                        change = { deleteAlert = false },
                        onDelete = { isChecked ->
                            if (isChecked) {
                                pridejNaSeznam()
                                onDelete()
                            }
                            onDelete()
                        }
                    )
                }
            }
        }
    }else
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(vertical = 4.dp, horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp)),
                onClick = { onConfirmClick() },
                colors = ButtonDefaults.buttonColors(cardGradient3),
            ) {
                Text(
                    text = "Přidat potravinu",
                    fontSize = 25.sp,
                    color = Color.Black
                )
            }
        }
}

@Composable
fun DeleteAlert2(
    change: () -> Unit,
    onDelete: (Boolean) -> Unit // Předáváme informaci, zda bylo políčko zaškrtnuté
) {
    var isChecked by remember { mutableStateOf(false) } // Stav pro zaškrtávací políčko

    AlertDialog(
        onDismissRequest = { change() },
        title = { Text("Opravdu chcete odstranit tuto potravinu?") },
        text = {
            Column {
                Text("Tato akce je nevratná.")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )
                    Text("Přidat potravinu do nákupního seznamu")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete(isChecked) // Vracíme stav zaškrtnutí
                    change()
                }
            ) {
                Text("odstranit", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { change() }
            ) {
                Text("Zrušit")
            }
        }
    )
}

@Composable
fun BottomBarPolozky(
    addKatalog: () -> Unit,
    onDone: () -> Unit
) {
    // Vytvoříme velké tlačítko přes celou šířku
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(15.dp))
                .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp))
                .background(buttonPodtvrdit),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .clickable { addKatalog() }
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.plus),
                    contentDescription = "Přidat položku",
                    modifier = Modifier.size(50.dp)
                )

                Text("Přidat položku", fontSize = 15.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider(modifier = Modifier.width(2.dp).height(65.dp), color = Color.Black, thickness = 2.dp)

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDone() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.done),
                    contentDescription = "done",
                    modifier = Modifier.height(50.dp).width(70.dp)
                )

                Text("Hotovo", fontSize = 15.sp, color = Color.Black)
            }
        }
    }
}