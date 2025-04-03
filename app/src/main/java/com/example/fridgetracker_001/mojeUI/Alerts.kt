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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.ui.theme.backgroundAlers
import com.example.fridgetracker_001.ui.theme.buttoncolor
import com.example.fridgetracker_001.ui.theme.cardGradient22
import com.example.fridgetracker_001.ui.theme.errorLight
import com.example.fridgetracker_001.ui.theme.onTertiaryContainerLight

@Composable
fun ViewTypeDialog(
    viewTypeDialogVisible: () -> Unit,
    listChange: () -> Unit,
    smallListChange: () -> Unit,
    gridChange: () -> Unit,
    localViewType: String
){
    AlertDialog(
        onDismissRequest = { viewTypeDialogVisible() },
        title = { Text(text = "Zvolte zobrazení položek") },
        text = {
            Column {
                Text(
                    text = "Seznam",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            listChange()
                            viewTypeDialogVisible()
                        }
                        .border(if (localViewType == "LIST") 2.dp else 0.dp, Color.Black)
                        .padding(8.dp)
                )
                Text(
                    text = "Kompaktní Seznam",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            smallListChange()
                            viewTypeDialogVisible()
                        }
                        .border(if (localViewType == "SMALL_LIST") 2.dp else 0.dp, Color.Black)
                        .padding(8.dp)
                )
                Text(
                    text = "Dlaždice",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            gridChange()
                            viewTypeDialogVisible()
                        }
                        .border(if (localViewType == "GRID") 2.dp else 0.dp, Color.Black)
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { viewTypeDialogVisible() }) {
                Text("Zavřít")
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
    countChange: () -> Unit
){
    AlertDialog(
        onDismissRequest = { sortDialogVisible() },
        title = { Text(text = "Zvolte serazeni") },
        text = {
            Column {
                Text("Potraviny", fontSize = 20.sp)
                Text(
                    text = "Jméno",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            nameChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp)
                )
                Text(
                    text = "Dny do data spotřeby",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dateExpiryChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp)
                )
                Text(
                    text = "Datum přidání",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dateAddedChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp)
                )

                Text("Kategorie", fontSize = 20.sp)
                Text(
                    text = "Abeceda",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            alphabetChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp)
                )
                Text(
                    text = "defaultne",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            defaultChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp)
                )
                Text(
                    text = "Podle mnozstvi potravin v kategorii",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            countChange()
                            sortDialogVisible()
                        }
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { sortDialogVisible() }) {
                Text("Zavřít")
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
        onDismissRequest = onDismiss,
        title = { Text(text = "Nastavení") },
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
                        text = "Zaškrni pro nastavení upozornění na datum spotřeby.",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "První varování:",
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
                        text = "Den/Dny před expirací",
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.7f),
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Druhé varování:",
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
                        text = "Den/Dny před expirací",
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.7f),
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }
            }
        },
        // Např. pokud chceš zachovat původní containerColor
        containerColor = backgroundAlers,
        confirmButton = {
            TextButton(
                onClick = {
                    // Zavoláme callback onSave s novými hodnotami
                    onSave(expirace1.ifBlank { "7" }, expirace2.ifBlank { "3" })
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = onTertiaryContainerLight,
                    contentColor = Color.White
                )
            ) {
                Text("Potvrdit změny")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = errorLight,
                    contentColor = Color.White
                )
            ) {
                Text("Zavřít")
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
    AlertDialog(
        onDismissRequest = aiDialogVisible,
        title = { Text("AI Prompt Builder") },
        text = {
            Column {
                // TextField #1
                TextField(
                    value = aiPromptTitle,
                    onValueChange = { aiPromptTitleChange(it) },
                    label = { Text("Znění promptu") }
                )

                // TextField #2
                TextField(
                    value = aiPotravinyText,
                    onValueChange = { aiPotravinyTextChange(it) },
                    label = { Text("Vybrané potraviny") }
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    // Tlačítko: "14 dní expirace"
                    Button(onClick = {
                        val potraviny14 = potravinaList.filter {
                            val daysLeftVal = getDaysLeft(it)
                            daysLeftVal != null && daysLeftVal <= 14
                        }
                        // Naplníme text
                        aiPotravinyTextChange(potraviny14.joinToString(", ") { it.nazev })
                    }) {
                        Text("14 dní expirace")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(onClick = {
                        onManualSelect()
                    }) {
                        Text("Vyber ručně")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val clipboardText = aiPromptTitle + "\n" + aiPotravinyText
                onCopy(clipboardText)
                aiDialogVisible()
            }) {
                Text("Kopírovat a Zavřít")
            }
        },
        dismissButton = {
            Button(onClick = { aiDialogVisible() }) {
                Text("Zrušit")
            }
        }
    )
}