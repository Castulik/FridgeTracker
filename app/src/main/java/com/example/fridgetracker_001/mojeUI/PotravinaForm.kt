package com.example.fridgetracker_001.mojeUI

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.IconRegistry
import com.example.fridgetracker_001.data.IconRegistry.kindOptions
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.ui.theme.bilaback
import com.example.fridgetracker_001.ui.theme.buttonPodtvrdit
import com.example.fridgetracker_001.ui.theme.buttoncolor
import com.example.fridgetracker_001.ui.theme.errorLight
import com.example.fridgetracker_001.ui.theme.surfaceContainerHighestLight
import com.example.fridgetracker_001.ui.theme.topmenu12
import com.example.fridgetracker_001.ui.theme.topmenu22
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotravinaFormBoxWithConstraints(
    potravina: PotravinaEntity,
    onPotravinaChange: (PotravinaEntity) -> Unit,
    isEdit: Boolean,
    isAdd: Boolean,
    onDialogChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit = {},
    pridejNaSeznam: () -> Unit = {},
    onScanBarcodeClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    //var add by rememberSaveable { mutableStateOf(isAdd) }
    var deleteAlert by remember { mutableStateOf(false) }
    val isError = false

    val gradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to topmenu12,
            0.6f to topmenu22,
        )
    )

    Scaffold(
        topBar = {
            TopBarPotravinaForm(
                isEdit = isEdit,
                onCancel = onCancel,
                onDelete = { onDelete() },
                pridejNaSeznam = { pridejNaSeznam()  }
            )
        },
        // Tady bude pevný bottomBar, který se nescrolluje
        bottomBar = {
            // Ten box s buttonem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent) // Můžeš dát i jinou barvu
                    .padding(vertical = 4.dp, horizontal = 10.dp),
            ) {
                Button(
                    shape = RoundedCornerShape(15.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp)),
                    onClick = {
                        onSubmit()
                    },
                    colors = ButtonDefaults.buttonColors(buttonPodtvrdit)
                ) {
                    Text(
                        text = if (isEdit) stringResource(R.string.pf_save_changes) else stringResource(R.string.pf_confirm),
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        // Obsah layoutu
        // MÍSTO Column(...weight(...)...) POUŽIJEME BoxWithConstraints
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    // Pokud bude layout moc vysoký, raději zapneme i scroll:
                    .verticalScroll(rememberScrollState())
            ) {
                // maxHeight = celková dostupná výška v DP
                val totalHeight = this.maxHeight

                // Můžeme si definovat "váhy" jednotlivých řádků:
                // (jen jako ukázka, abychom navázali na původní .weight(1f), .weight(0.7f) atd.)
                val row1Weight = 1f     // "Název + druh + VyberObrazku"
                val row2Weight = 0.5f   // "Barcode field + barcode img"
                val row3Weight = 0.5f   // "Množství"
                val row4Weight = 1f     // "Hmotnost"
                val row5Weight = 0.5f   // "Datum potřeby"
                val row6Weight = 0.5f   // "Datum přidání"
                val row7Weight = 1f   // "Poznámka"

                // Spočítáme sumu
                val sumWeights = row1Weight + row2Weight + row3Weight + row4Weight +
                        row5Weight + row6Weight + row7Weight

                // Funkce, která přepočítá weight na konkrétní DP
                fun rowHeight(weight: Float) = totalHeight * (weight / sumWeights)

                // Následně budeme stackovat "Boxy" (nebo Column/Row) pod sebe:
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    // 1) DRUH, NÁZEV POTRAVINY + OBRÁZEK
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight(row1Weight))
                    ) {
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current

                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .heightIn(min = 120.dp, max = 200.dp)
                                    .weight(0.7f),
                                verticalArrangement = Arrangement.SpaceBetween, // jeden prvek nahoru, druhý dolů
                                horizontalAlignment = Alignment.Start
                            ) {
                                UnderlinedTextSelector(
                                    selectedOption = potravina.druh,
                                    onOptionSelected = { onPotravinaChange(potravina.copy(druh = it)) },
                                    dialogChange = isAdd,
                                    onDialogChange = { onDialogChange(it) }
                                )

                                MujTextField(
                                    value = potravina.nazev,
                                    onValueChange = { onPotravinaChange(potravina.copy(nazev = it)) },
                                    placeholder = stringResource(R.string.pf_name_placeholder),
                                    isError = isError,
                                    errorMessage = "",
                                    maxLength = 30,
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
                                        focusedContainerColor = Color(0xFF9BD46A),
                                        unfocusedContainerColor = Color(0xFF9BD46A),
                                        disabledContainerColor = Color(0xFF9BD46A),
                                        errorContainerColor = Color(0xFF9BD46A),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        cursorColor = Color.Black,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        errorTextColor = Color.Red
                                    )
                                )
                            }

                            VyberObrazku(
                                potravinaIconaId = potravina.potravinaIconaId,
                                onIconSelect = { onPotravinaChange(potravina.copy(potravinaIconaId = it)) },
                            )
                        }
                    }

                    // 2) BARCODE
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight(row2Weight))
                    ) {
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            MujTextField(
                                value = potravina.code,
                                onValueChange = { onPotravinaChange(potravina.copy(code = it)) },
                                placeholder = stringResource(R.string.pf_barcode_placeholder),
                                isError = isError,
                                errorMessage = "",
                                maxLength = 20,
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
                                    .weight(0.7f)
                                    .clip(MaterialTheme.shapes.small)
                                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF9BD46A),
                                    unfocusedContainerColor = Color(0xFF9BD46A),
                                    disabledContainerColor = Color(0xFF9BD46A),
                                    errorContainerColor = Color(0xFF9BD46A),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    errorTextColor = Color.Red
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 3.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(bilaback)
                                    .clickable {
                                        onScanBarcodeClick()
                                    }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(R.drawable.barcode)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillHeight,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Oddelovac2()

                    // 3) MNOŽSTVÍ
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight(row3Weight))
                    ) {
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            MnozstviVyber(
                                mnozstvi = potravina.mnozstvi,
                                onMnozstviChange = { onPotravinaChange(potravina.copy(mnozstvi = it)) },
                                isError = isError,
                                focusManager = focusManager,
                                keyboardController = keyboardController
                            )
                        }
                    }

                    Oddelovac2()

                    // 4) HMOTNOST
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            //.background(Color.Red)
                            //.heightIn(min = 200.dp)
                            .height(rowHeight(row4Weight))
                    ) {
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current

                        Row(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            HmotnostVyber(
                                vaha = potravina.vaha,
                                onVahaChange = { onPotravinaChange(potravina.copy(vaha = it)) },
                                jednotka = potravina.jednotky,
                                onJednotkyChange = { onPotravinaChange(potravina.copy(jednotky = it)) },
                                focusManager = focusManager,
                                keyboardController = keyboardController
                            )
                        }
                    }

                    Oddelovac2()

                    // 5) DATUM POTŘEBY
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight(row5Weight))
                    ) {
                        DatePickerField(
                            dateValue = potravina.datumSpotreby,
                            onDateChange = { onPotravinaChange(potravina.copy(datumSpotreby = it)) },
                            label = stringResource(R.string.pf_exp_date),
                            dateFormatter = dateFormatter,
                        )
                    }

                    // 6) DATUM PŘIDÁNÍ
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight(row6Weight))
                    ) {
                        DatePickerField(
                            dateValue = potravina.datumPridani,
                            onDateChange = { onPotravinaChange(potravina.copy(datumPridani = it)) },
                            label = stringResource(R.string.pf_added_date),
                            dateFormatter = dateFormatter,
                            initialDateMillis = today.toEpochDay() * 24 * 60 * 60 * 1000
                        )
                    }

                    // 7) POZNÁMKA
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight(row7Weight))
                    ) {
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current
                        OutlinedTextField(
                            value = potravina.poznamka,
                            onValueChange = { onPotravinaChange(potravina.copy(poznamka = it)) },
                            placeholder = {
                                Text(stringResource(R.string.pf_note_placeholder), fontSize = 20.sp, color = Color.Black)
                            },
                            modifier = Modifier
                                .fillMaxSize(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = bilaback,
                                unfocusedContainerColor = bilaback,
                                disabledContainerColor = bilaback,
                                errorContainerColor = bilaback,
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                errorTextColor = Color.Red,

                                ),
                            maxLines = 5,
                            singleLine = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            )
                        )
                    }

                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)    // zarovnat nahoru (uprostřed)
                    .padding(top = 8.dp)         // třeba mírný odskok
            )
        }
    }
}

@Composable
fun Oddelovac2(){
    Column {
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(2.dp))
    }
}

fun isValidDecimal(input: String): Boolean {
    // Povolit prázdný vstup pro počáteční zadávání
    if (input.isEmpty()) return true

    // Nahradit čárku tečkou pro jednotný formát
    val standardizedInput = input.replace(',', '.')

    // Regulární výraz pro kladné desetinné číslo
    val decimalPattern = "^[0-9]+(\\.[0-9]*)?$".toRegex()

    return decimalPattern.matches(standardizedInput)
}

fun getDrawableResourcesStartingWith(prefix: String, context: Context): List<Int> {
    val drawableClass = R.drawable::class.java
    val resources = mutableListOf<Int>()

    // Iterace přes všechny atributy třídy R.drawable
    for (field in drawableClass.declaredFields) {
        val name = field.name // Název zdroje (např. "food_apple")
        if (name.startsWith(prefix)) {
            try {
                // Získání resource ID
                val resourceId = field.getInt(null)
                resources.add(resourceId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return resources
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    dateValue: String,
    onDateChange: (String) -> Unit,
    dateFormatter: DateTimeFormatter,
    initialDateMillis: Long? = null,
) {
    // Ikonka kalendáře
    val calendar = ImageVector.vectorResource(id = R.drawable.calendar)
    var showDatePicker by remember { mutableStateOf(false) }

    // TextField, do kterého se nebude psát, ale vybere se v date pickeru
    OutlinedTextField(
        value = dateValue,
        onValueChange = {}, // zakázané, řídíme to jen date pickerem
        label = { Text(text = label, fontSize = 20.sp, color = Color.Black) },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = calendar,
                contentDescription = "Vyberte datum",
                modifier = Modifier.clickable {
                    showDatePicker = true
                }
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Red,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            errorTextColor = Color.Red,
            focusedTrailingIconColor = Color.Black,
            unfocusedTrailingIconColor = Color.Black,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDatePicker = true // při kliknutí na celé pole otevřít dialog
            }
    )

    // Pokud je dialog otevřen, zobrazíme DatePickerDialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val selectedDate =
                                LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                            // Po výběru data zavoláme callback:
                            onDateChange(selectedDate.format(dateFormatter))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.pf_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.dlg_cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = true
            )
        }
    }
}

@Composable
fun HmotnostVyber(
    vaha: String,
    onVahaChange: (String) -> Unit,
    jednotka: String,
    onJednotkyChange: (String) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            MujTextField(
                value = vaha,
                onValueChange = { newValue ->
                    // Povolit pouze číslice a desetinnou čárku/tečku
                    val filtr = newValue.filter { it.isDigit() || it == '.' || it == ',' }
                    val standardizedValue = filtr.replace(',', '.')
                    if (isValidDecimal(standardizedValue)) {
                        onVahaChange(standardizedValue)
                    }
                },
                placeholder = stringResource(R.string.pf_weight_placeholder),
                isError = !isValidDecimal(vaha),
                errorMessage = "",
                maxLength = 4,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small)
                    .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF9BD46A),
                    unfocusedContainerColor = Color(0xFF9BD46A),
                    disabledContainerColor = Color(0xFF9BD46A),
                    errorContainerColor = Color(0xFF9BD46A),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Red
                )
            )

            val jednotky = listOf("kg", "g", "ml")

            // Výběr jednotky pomocí tlačítek
            jednotky.forEach { j ->
                Button(
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = { onJednotkyChange(j) },
                    modifier = Modifier
                        .weight(0.4f)
                        .height(45.dp)
                        .padding(horizontal = 5.dp)
                        .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (jednotka == j) buttoncolor else Color(0xFF9BD46A)
                    ),
                ) {
                    Text(
                        text = j,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val kilogramy = listOf(1, 2, 3, 4, 5)
            val gramy = listOf(50, 100, 200, 500, 700)
            val ml = listOf(50, 100, 250, 500, 1000)

            val hodnoty = when (jednotka){
                "kg" -> kilogramy
                "g" -> gramy
                "ml" -> ml
                else -> emptyList()
            }

            hodnoty.forEach { hodnota ->
                Button(
                    shape = RoundedCornerShape(15.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        onVahaChange(hodnota.toString())
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                        .padding(horizontal = 5.dp)
                        .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp)), // Shodný tvar pro border
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vaha == hodnota.toString()) buttoncolor else Color(0xFF9BD46A)
                    ),
                ) {
                    Text(
                        text = hodnota.toString(),
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
        }

    }
}

@Composable
fun MnozstviVyber(
    mnozstvi: String,
    onMnozstviChange: (String) -> Unit,
    isError: Boolean = false,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {

    fun getNumericValue(): Int {
        return mnozstvi.toIntOrNull() ?: 0
    }

    fun updateMnozstvi(newMnozstvi: Int) {
        onMnozstviChange(newMnozstvi.toString())
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val minus = listOf(
            Pair("-5") { if (getNumericValue() >= 5) updateMnozstvi(getNumericValue() - 5) },
            Pair("-1") { if (getNumericValue() >= 1) updateMnozstvi(getNumericValue() - 1) },
        )

        minus.forEach { (label, action) ->
            Button(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttoncolor),
                onClick = action,
                modifier = Modifier
                    .weight(0.5f)
                    .height(45.dp)
                    .padding(horizontal = 5.dp)
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = label, fontSize = 20.sp, color = Color.Black)
            }
        }

        MujTextField(
            value = mnozstvi,
            onValueChange = { newValue ->
                onMnozstviChange(newValue.filter { it.isDigit() }) // Povolit pouze číslice
            },
            placeholder = stringResource(R.string.pf_amount_placeholder),
            isError = isError,
            errorMessage = "",
            maxLength = 4,
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
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.small)
                .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF9BD46A),
                unfocusedContainerColor = Color(0xFF9BD46A),
                disabledContainerColor = Color(0xFF9BD46A),
                errorContainerColor = Color(0xFF9BD46A),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Red
            )
        )

        val plus = listOf(
            Pair("+1") { updateMnozstvi(getNumericValue() + 1) },
            Pair("+5") { updateMnozstvi(getNumericValue() + 5) },
        )

        plus.forEach { (label, action) ->
            Button(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttoncolor),
                onClick = action,
                modifier = Modifier
                    .weight(0.5f)
                    .height(45.dp)
                    .padding(horizontal = 5.dp)
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = label, fontSize = 20.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun VyberObrazku(
    potravinaIconaId: Int,
    onIconSelect: (Int) -> Unit
) {
    var vyberObrazku by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 3.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .background(bilaback)
            .clickable { vyberObrazku = true }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(potravinaIconaId)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }

    // Dialog pro výběr obrázku POTRAVINY
    if (vyberObrazku) {
        Dialog(
            onDismissRequest = { vyberObrazku = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Zajistí full-screen šířku
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(surfaceContainerHighestLight, shape = RoundedCornerShape(16.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(stringResource(R.string.pf_imageChoice), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    /*
                    val context = LocalContext.current
                    // Vytvoří se jen jednou během životnosti composable,
                    // dokud se nevyrestartuje celý (např. při rotation).
                    val images = remember {
                        getDrawableResourcesStartingWith("food_", context)
                    }
                     */
                    val images = IconRegistry.foodList

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(images) { image ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        4.dp,
                                        if (image == potravinaIconaId) Color.Black else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(1.dp)
                                    .background(Color.White)
                                    .clickable {
                                        onIconSelect(image)
                                        vyberObrazku = false
                                    }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(image)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { vyberObrazku = false },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(errorLight)
                    ) {
                        Text(stringResource(R.string.dlg_cancel))
                    }
                }
            }
        }
    }
}

@Composable
fun UnderlinedTextSelector(
    @StringRes selectedOption: Int?,
    onOptionSelected: (Int) -> Unit,
    dialogChange: Boolean,
    onDialogChange: (Boolean) -> Unit
) {
    val options = listOf("Mražené potraviny", "Trvanlivé potraviny", "Ovoce", "Zelenina", "Mléčné výrobky", "Maso a Ryby", "Pečivo", "Vejce", "Obiloviny",
        "Luštěniny","Ořechy", "Nápoje", "Koření","Omáčky", "Hotová jídla", "Ostatní")

    val options2 = listOf("Mražené", "Trvanlivé", "Ovoce a Zelenina", "Mléčné výrobky", "Maso a Ryby", "Pečivo", "Vejce", "Obiloviny a luštěniny",
        "Uzeniny a lahůdky", "Nápoje", "Hotová jídla", "Ostatní")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable { onDialogChange(true) }
    ) {
        Text(
            text = selectedOption?.let { stringResource(it) } ?: "",
            fontSize = 25.sp,
            color = Color.Black,
            modifier = Modifier
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )
                }
        )
    }

    if (dialogChange) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.pf_kindChoice)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(kindOptions) { option ->
                            val optionSelected  = option.nameRes == selectedOption

                            Column(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .border(
                                        width = 2.dp,
                                        color = if (optionSelected) Color.Black else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        onOptionSelected(option.nameRes)
                                        onDialogChange(false)
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
                                        contentDescription = stringResource(option.nameRes),
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                                    )
                                }
                                // 3) Spodní část pro text: zabere 25 % výšky buňky
                                Box(
                                    modifier = Modifier
                                        .weight(0.5f, fill = true)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(option.nameRes),
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp,
                                        minLines = 2,                       // dovolíme až 2 řádky
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
        )
    }
}