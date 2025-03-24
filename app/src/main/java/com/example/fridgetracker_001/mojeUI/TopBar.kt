package com.example.fridgetracker_001.mojeUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.cardGradient3

import com.example.fridgetracker_001.ui.theme.topmenu1
import com.example.fridgetracker_001.ui.theme.topmenu12
import com.example.fridgetracker_001.ui.theme.topmenu2
import com.example.fridgetracker_001.ui.theme.topmenu22

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    nazevSkladu: String,
    onBackClick: () -> Unit,
    onAiClick: () -> Unit,
    menuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onSortClicked: () -> Unit,
    onViewTypeClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onEditClick: () -> Unit,
){
    val gradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to topmenu1,
            0.6f to topmenu2,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
    ) {
        MediumTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.Black,
            ),
            title = {
                Text(
                    text = nazevSkladu,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                IconButton( onClick =  onBackClick ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zpět",
                        tint = Color.Black
                    )
                }
            },
            actions = {

                IconButton( onClick = onEditClick ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.multiple),
                        contentDescription = "ai",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }

                IconButton( onClick = onAiClick ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ai),
                        contentDescription = "ai",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(40.dp)
                    )
                }

                IconButton( onClick = {onMenuExpandedChange(true)} ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "nastaveni skladu",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
                // Samotné DropdownMenu
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { onMenuExpandedChange(false) },
                    offset = DpOffset(x = (40).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Seřadit") },
                        onClick = {
                            onSortClicked()
                            onMenuExpandedChange(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Vzhled") },
                        onClick = {
                            onViewTypeClicked()
                            onMenuExpandedChange(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Nastavení") },
                        onClick = {
                            onSettingsClicked()
                            onMenuExpandedChange(false)
                        }
                    )
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectTopBar(
    selectedCount: Int,
    onBackClick: () -> Unit,
    ai: Boolean
) {
    MediumTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if(!ai) Color.LightGray else cardGradient3,
            titleContentColor = Color.Black,
        ),
        title = { Text("Vybráno: $selectedCount") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět z multiselectu",tint = Color.Black)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarPotravinaForm(
    isEdit: Boolean,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    pridejNaSeznam: () -> Unit,
) {
    // Místní stav pro potvrzení mazání
    var deleteAlert by remember { mutableStateOf(false) }

    // Gradient můžeš definovat klidně přímo tady
    val gradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to topmenu12,
            0.6f to topmenu22,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isEdit) "Upravit potravinu" else "Nová potravina",
                    fontSize = 25.sp,
                )
            },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět", tint = Color.Black)
                }
            },
            actions = {
                if (isEdit) {
                    IconButton(onClick = { deleteAlert = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    if (deleteAlert) {
                        DeleteAlert2(
                            change = { deleteAlert = false },
                            onDelete = { isChecked ->
                                if (isChecked) {
                                    pridejNaSeznam()
                                    onDelete()
                                } else
                                    onDelete()
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.Black,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkladFormTopBar(
    isEdit: Boolean,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
){
    var deleteAlert by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardGradient22)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isEdit) "Upravit skladiště" else "Přidat skladiště",
                    fontSize = 25.sp,
                )
            },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět", tint = Color.Black)
                }
            },
            actions = {
                if (isEdit) {
                    IconButton(onClick = { deleteAlert = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    if (deleteAlert) {
                        AlertDialog(
                            onDismissRequest = { deleteAlert = false },
                            title = { Text("Opravdu chcete smazat tento sklad?") },
                            text = { Text("Tato akce je nevratná.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onDelete()
                                        /*
                                        skladViewModel.smazatSklad(sklad)
                                        navController.navigate("mojepotraviny")
                                        smazatSklad = false
                                        onDismiss()

                                         */
                                    }
                                ) {
                                    Text("Smazat", color = Color.Red)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { deleteAlert = false }
                                ) {
                                    Text("Zrušit")
                                }
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.Black,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeznamTopBar(
    onPridatNakup: () -> Unit,
    onNastaveni: () -> Unit,
    onHistoryClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = cardGradient22,
            titleContentColor = Color.Black,
        ),
        title = { Text("Nákupní seznam") },
        actions = {
            IconButton(onClick = onPridatNakup) {
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.addnakup), contentDescription = "Odebrat", modifier = Modifier.size(25.dp), tint = Color.Black)
            }
            IconButton(onClick = onNastaveni) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "nastaveni",tint = Color.Black)
            }
            IconButton(onClick = onHistoryClick) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "history",tint = Color.Black)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeznamTopBar(
    type: Boolean,
    onCancel: () -> Unit,
){
    TopAppBar(
        title = {
            Text(
                text = if (type) "Historie nakupu" else "Přidej položky na nakup",
                fontSize = 25.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět", tint = Color.Black)
            }
        },
        actions = {
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = cardGradient3,
            titleContentColor = Color.Black,
        )
    )
}
