package com.example.fridgetracker_001.mojeUI

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.ui.theme.backgroundAlers
import com.example.fridgetracker_001.ui.theme.buttoncolor
import com.example.fridgetracker_001.ui.theme.cardGradient2
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.errorLight
import com.example.fridgetracker_001.ui.theme.onTertiaryContainerLight
import com.example.fridgetracker_001.ui.theme.primaryLight

@Composable
fun ViewTypeDialog(
    viewTypeDialogVisible: () -> Unit,
    listChange: () -> Unit,
    smallListChange: () -> Unit,
    gridChange: () -> Unit,
    localViewType: String
) {
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = { viewTypeDialogVisible() },
        title = { Text(text = stringResource(R.string.view_type_dialog_title), color = Color.Black) },
        text = {
            Column {
                // Seznam
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            listChange()
                            viewTypeDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = localViewType == "LIST",
                        onClick = null // nechceme duplikovat klikání
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.list), color = Color.Black)
                }

                // Kompaktní seznam
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            smallListChange()
                            viewTypeDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = localViewType == "SMALL_LIST",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.compact_list), color = Color.Black)
                }

                // Dlaždice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            gridChange()
                            viewTypeDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = localViewType == "GRID",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.grid), color = Color.Black)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewTypeDialogVisible() }) {
                Text(stringResource(R.string.close), color = primaryLight)
            }
        }
    )
}


@Composable
fun SortDialog(
    sortDialogVisible: () -> Unit,
    nameChange: () -> Unit,
    dateExpiryChange: () -> Unit,
    dateAddedChange: () -> Unit,
    alphabetChange: () -> Unit,
    defaultChange: () -> Unit,
    countChange: () -> Unit,
    kategorie: String?,
    potraviny: String?,
) {
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = { sortDialogVisible() },
        title = { Text(text = stringResource(R.string.sort_dialog_title), color = Color.Black) },
        text = {
            Column {
                Text(stringResource(R.string.sort_food), fontSize = 20.sp, color = Color.Black)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            nameChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = potraviny == "NAME",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.name), color = Color.Black)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dateExpiryChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = potraviny == "DATE_EXPIRY",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.expiry_date), color = Color.Black)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dateAddedChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = potraviny == "DATE_ADDED",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.added_date), color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.sort_categories), fontSize = 20.sp, color = Color.Black)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            alphabetChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = kategorie == "ALPHABETICAL",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.alphabetical), color = Color.Black)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            defaultChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = kategorie == "DEFAULT",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.defaults), color = Color.Black)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            countChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = kategorie == "COUNT",
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.count), color = Color.Black)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { sortDialogVisible() }) {
                Text(stringResource(R.string.close), color = primaryLight)
            }
        }
    )
}


@Composable
fun NastaveniDialog(
    sklad: SkladEntity?,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    // Např. inicializace na "7" a "3", pokud je sklad null
    val initialExp1 = sklad?.expirace1 ?: "7"
    val initialExp2 = sklad?.expirace2 ?: "3"
    // Lokální stavy (remember)
    var expirace1 by remember { mutableStateOf(initialExp1) }
    var expirace2 by remember { mutableStateOf(initialExp2) }
    var upozorneniZapnuto by remember { mutableStateOf(false) }
    // Pokud potřebuješ, můžeš sem hodit i isError atp.
    val isError = false // příklad
    // Barvy, co jsi měl dřív (můžeš je sdílet přes nějakou samostatnou "const val",
    // nebo je klidně nechat tady)
    val baseYellow = Color(0xFFFFFF2F)
    val baseRed = Color(0xFFEF5F5F)
    val disabledYellow = baseYellow.copy(alpha = 0.5f)
    val disabledRed = baseRed.copy(alpha = 0.5f)

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.notification_settings), color = Color.Black) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = upozorneniZapnuto,
                        onCheckedChange = { upozorneniZapnuto = it }
                    )
                    Text(
                        text = stringResource(R.string.check_to_enable_notifications),
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.first_warning),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current

                    MujTextField(
                        value = expirace1,
                        onValueChange = { newValue ->
                            expirace1 = newValue.filter { it.isDigit() }
                        },
                        placeholder = "",
                        isError = isError,
                        singleLine = true,
                        errorMessage = "",
                        maxLength = 3,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        enabled = upozorneniZapnuto,
                        modifier = Modifier
                            .weight(0.3f)
                            .clip(MaterialTheme.shapes.small)
                            .border(
                                2.dp,
                                Color.Black,
                                shape = MaterialTheme.shapes.small
                            ),
                        colors = TextFieldDefaults.colors(
                            // Při zapnutém checkboxu jasně žluté, při vypnutém slabě žluté
                            focusedContainerColor = if (upozorneniZapnuto) baseYellow else disabledYellow,
                            unfocusedContainerColor = if (upozorneniZapnuto) baseYellow else disabledYellow,
                            disabledContainerColor = disabledYellow,
                            errorContainerColor = if (upozorneniZapnuto) baseYellow else disabledYellow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            disabledTextColor = Color.DarkGray,
                            errorTextColor = Color.Red
                        )
                    )
                    Text(
                        text = stringResource(R.string.days_before_expiry),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.7f),
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.second_warning),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current

                    MujTextField(
                        value = expirace2,
                        onValueChange = { newValue ->
                            expirace2 = newValue.filter { it.isDigit() }
                        },
                        placeholder = "",
                        singleLine = true,
                        isError = isError,
                        errorMessage = "",
                        maxLength = 3,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        enabled = upozorneniZapnuto,
                        modifier = Modifier
                            .weight(0.3f)
                            .clip(MaterialTheme.shapes.small)
                            .border(
                                2.dp,
                                Color.Black,
                                shape = MaterialTheme.shapes.small
                            ),
                        colors = TextFieldDefaults.colors(
                            // Při zapnutém checkboxu jasně červené, při vypnutém slabě červené
                            focusedContainerColor = if (upozorneniZapnuto) baseRed else disabledRed,
                            unfocusedContainerColor = if (upozorneniZapnuto) baseRed else disabledRed,
                            disabledContainerColor = disabledRed,
                            errorContainerColor = if (upozorneniZapnuto) baseRed else disabledRed,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            disabledTextColor = Color.DarkGray,
                            errorTextColor = Color.Red
                        )
                    )
                    Text(
                        text = stringResource(R.string.days_before_expiry),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.7f),
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Zavoláme callback onSave s novými hodnotami
                    onSave(expirace1.ifBlank { "7" }, expirace2.ifBlank { "3" })
                },
            ) {
                Text(stringResource(R.string.confirm_changes), color = primaryLight)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(R.string.close), color = primaryLight)
            }
        }
    )
}

@Composable
fun AiPromptDialog(
    aiPromptTitle: String,
    aiPromptTitleChange: (String) -> Unit,
    aiPotravinyText: String,
    aiPotravinyTextChange: (String) -> Unit,
    potravinaList: List<PotravinaEntity>,
    getDaysLeft: (PotravinaEntity) -> Long?,
    onManualSelect: () -> Unit,
    onCopy: (String) -> Unit,
    aiDialogVisible: () -> Unit,
) {
    val isError = false

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = aiDialogVisible,
        title = { Text(stringResource(R.string.ai_prompt_title), color = Color.Black) },
        text = {

            Column {
                Text(stringResource(R.string.prompt_content), fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.height(10.dp))

                Row {
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current
                    MujTextField(
                        value = aiPromptTitle,
                        onValueChange = { aiPromptTitleChange(it) },
                        placeholder = stringResource(R.string.prompt_placeholder),
                        isError = isError,
                        errorMessage = "",
                        maxLength = 1000,
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
                            focusedContainerColor = cardGradient2,
                            unfocusedContainerColor = cardGradient2,
                            disabledContainerColor = cardGradient2,
                            errorContainerColor = cardGradient2,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Red
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.selected_food), fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.height(10.dp))

                Row{
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current
                    MujTextField(
                        value = aiPotravinyText,
                        onValueChange = { aiPotravinyTextChange(it) },
                        placeholder = "",
                        isError = isError,
                        errorMessage = "",
                        maxLength = 1000,
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
                            focusedContainerColor = cardGradient2,
                            unfocusedContainerColor = cardGradient2,
                            disabledContainerColor = cardGradient2,
                            errorContainerColor = cardGradient2,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Red
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.select_food_with_buttons), fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.height(5.dp))

                Row {
                    // Tlačítko: Potraviny s expirací do 14 dnů
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .border(width = 2.dp, color = Color(0xFF87F4FF), shape = MaterialTheme.shapes.small),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        onClick = {
                            val potraviny14 = potravinaList.filter {
                                val daysLeftVal = getDaysLeft(it)
                                daysLeftVal != null && daysLeftVal <= 14
                            }
                            aiPotravinyTextChange(potraviny14.joinToString(", ") { it.nazev })
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF026FB6), // tmavší zelená
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.food_within_14_days),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // Tlačítko: Vybrat potraviny ručně
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .border(width = 2.dp, color = Color(0xFF87F4FF), shape = MaterialTheme.shapes.small),
                        onClick = { onManualSelect() },
                        shape = MaterialTheme.shapes.small,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF026FB6), // tmavší zelená
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.select_food_manually),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val clipboardText = aiPromptTitle + "\n" + aiPotravinyText
                    onCopy(clipboardText)
                    aiDialogVisible()
                },
            ) {
                Text(stringResource(R.string.copy_to_clipboard), color = primaryLight)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    aiDialogVisible()
                },
            ) {
                Text(stringResource(R.string.close), color = primaryLight)
            }
        }
    )
}

@Composable
fun DeleteAlert2(
    change: () -> Unit,
    onDelete: (Boolean) -> Unit // Předáváme informaci, zda bylo políčko zaškrtnuté
) {
    var isChecked by remember { mutableStateOf(false) } // Stav pro zaškrtávací políčko

    AlertDialog(
        onDismissRequest = { change() },
        title = { Text(stringResource(R.string.confirm_delete_title)) },
        text = {
            Column {
                Text(stringResource(R.string.confirm_delete_text))
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )
                    Text(stringResource(R.string.add_to_shopping_list))
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
                Text(stringResource(R.string.delete), color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { change() }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}