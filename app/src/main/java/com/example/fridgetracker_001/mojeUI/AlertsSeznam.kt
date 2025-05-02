package com.example.fridgetracker_001.mojeUI

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.IconRegistry
import com.example.fridgetracker_001.data.KindOptionEnum
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.ui.theme.buttoncolor
import com.example.fridgetracker_001.ui.theme.cardGradient3
import com.example.fridgetracker_001.ui.theme.cardPozadi
import com.example.fridgetracker_001.ui.theme.primaryLight

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, KindOptionEnum, Int) -> Unit = { _, _, _ -> },
    onConfirm2: (String, KindOptionEnum) -> Unit = { _, _ -> },
    onNavigate: () -> Unit = {},
    option: Boolean = false,
    isEdit: Boolean = false,
    isOnNakup: Boolean = false,
    isDelete: Boolean = false,
    onDelete: () -> Unit = {},
    nazev: String = "",
    kategorie: KindOptionEnum? = null,
    mnozstvi: Int = 1,
) {
    var text by remember { mutableStateOf(nazev) }
    var selectedCategory by remember { mutableStateOf(kategorie) }
    var quantity by remember { mutableIntStateOf(mnozstvi) }
    var showError by remember { mutableStateOf(false) }
    val isError by remember { mutableStateOf(false) }
/*
    val options2 = listOf(
        "Mražené", "Trvanlivé", "Ovoce a Zelenina", "Mléčné výrobky",
        "Maso a Ryby", "Pečivo", "Vejce", "Obiloviny a luštěniny",
        "Uzeniny a lahůdky", "Nápoje", "Hotová jídla", "Ostatní"
    )
 */

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {  Text(text = stringResource(id = if (isEdit) R.string.as_dialog_title_edit else R.string.as_dialog_title_add), color = Color.Black) },
        containerColor = Color.White,
        text = {
            Column {
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current

                    MujTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = stringResource(R.string.as_placeholder_item_name),
                        isError = isError,
                        errorMessage = "",
                        maxLength = 40,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = cardPozadi,
                            unfocusedContainerColor = cardPozadi,
                            disabledContainerColor = cardPozadi,
                            errorContainerColor = cardPozadi,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Red
                        )
                    )

                if(option) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {

                            Text(
                                text = "$quantity",
                                fontSize = 30.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(0.8f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardPozadi) // jemná tyrkysová
                                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                style = LocalTextStyle.current.copy(
                                    shadow = Shadow(
                                        color = Color.Gray,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    )
                                )
                            )


                            Button(
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                                onClick = {
                                    if (quantity > 1) quantity--
                                },
                                enabled = quantity > 1,
                                modifier = Modifier
                                    .weight(0.5f)
                                    .height(50.dp)
                                    .padding(start = 15.dp)
                                    .border(2.dp, Color.Black, shape = RoundedCornerShape(10.dp)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.minus), // Ikona ruky (můžete změnit)
                                    contentDescription = "Click Icon",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Black
                                )
                            }

                            Button(
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                                onClick = {
                                    quantity++
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .height(50.dp)
                                    .padding(start = 15.dp)
                                    .border(2.dp, Color.Black, shape = RoundedCornerShape(10.dp)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add, // Ikona ruky (můžete změnit)
                                    contentDescription = "Click Icon",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = if (isEdit) stringResource(R.string.as_dialog_category_edit) else stringResource(R.string.as_dialog_category_select), color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(KindOptionEnum.entries.filter { it != KindOptionEnum.UNKNOWN }) { category ->
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clickable {
                                    selectedCategory = category
                                    showError = false
                                }
                                .border(
                                    width = if (selectedCategory == category) 3.dp else 1.dp,
                                    color = if (selectedCategory == category) cardPozadi else Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp))
                                .padding(8.dp)
                                .aspectRatio(1.5f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = category.stringRes),
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if(option) {
                    Row {
                        Button(
                            onClick = { onNavigate();onDismiss() },
                            // Tady si nastavíte design tlačítka
                            shape = RoundedCornerShape(10.dp),   // zaoblené rohy
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00BCD4), // tyrkysová
                                contentColor = Color.White          // barva textu
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                .border(2.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add, // Ikona ruky (můžete změnit)
                                    contentDescription = "Click Icon",
                                    modifier = Modifier.size(30.dp)
                                )
                                Text(
                                    text = if (isEdit) stringResource(R.string.as_button_edit_from_catalog) else stringResource(
                                        R.string.as_button_add_from_catalog
                                    ),
                                    fontSize = 17.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                if (isDelete){
                    Text(text = stringResource(R.string.as_dialog_delete_item), color = Color.Black)
                    IconButton(onClick = {
                        onDelete()
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete from katalog",
                            tint = Color.Red,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.as_dialog_error_no_category),
                        color = Color.Red,
                        fontSize = 15.sp
                    )
                }

                if (isOnNakup) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.as_dialog_already_in_list),
                        color = Color.Red,
                        fontSize = 15.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedCategory != null) {
                        onConfirm(text, selectedCategory!!, quantity)
                        onConfirm2(text, selectedCategory!!)
                    } else {
                        showError = true
                    }
                }
            ) {
                Text(if (isEdit) stringResource(R.string.as_button_save) else stringResource(R.string.as_button_add), color = primaryLight)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = primaryLight)
            }
        }
    )
}



@Composable
fun SkladDialog(
    item: SeznamEntity,        // typ podle toho, co máte
    skladList: List<SkladEntity>,     // typ skladů
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit    // vracíme skladId
) {
    var selectedSklad by remember { mutableStateOf<SkladEntity?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.as_dialog_add_item_to_sklad, item.nazev))
        },
        text = {
            Column {
                Text(stringResource(R.string.as_select_storage))
                Spacer(modifier = Modifier.height(8.dp))
                // Jednoduchý výčet skladů s RadioButton
                skladList.forEach { sklad ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSklad = sklad }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (sklad == selectedSklad),
                            onClick = { selectedSklad = sklad }
                        )
                        Text(
                            text = sklad.nazev,    // nebo sklad.name, podle vašeho modelu
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // voláme onConfirm s ID vybraného skladu
                    selectedSklad?.let { onConfirm(it.id) }
                },
                enabled = (selectedSklad != null)  // tlačítko je aktivní jen pokud je vybrán sklad
            ) {
                Text(stringResource(R.string.as_button_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun SmazatAlert(
    change: () -> Unit,
    onDelete: () -> Unit
) {

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = { change() },
        title = { Text(stringResource(R.string.as_alert_delete_title), color = Color.Black) },
        text = {
            Column {
                Text(stringResource(R.string.as_alert_delete_text), color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete()
                    change()
                }
            ) {
                Text(stringResource(R.string.as_button_delete), color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { change() }
            ) {
                Text(stringResource(R.string.cancel), color = primaryLight)
            }
        }
    )
}

@Composable
fun NakupAlert(
    onAdd: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.as_alert_new_nakup_title), color = Color.Black) },
        text = {
            Text(stringResource(R.string.as_alert_new_nakup_text), color = Color.Black)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAdd()
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.as_button_add), color = primaryLight)
            }
        },
        dismissButton = {
            TextButton(onClick = {onDismiss()}
            ) {
                Text(stringResource(R.string.cancel), color = primaryLight)
            }
        }
    )
}