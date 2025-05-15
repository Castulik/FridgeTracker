package cz.filip.fridgetracker_001.mojeUI

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import cz.filip.fridgetracker_001.R
import cz.filip.fridgetracker_001.data.SkladIcon
import cz.filip.fridgetracker_001.data.entities.SkladEntity
import cz.filip.fridgetracker_001.ui.theme.buttonPodtvrdit
import cz.filip.fridgetracker_001.ui.theme.cardGradient22
import cz.filip.fridgetracker_001.ui.theme.cardGradient3
import cz.filip.fridgetracker_001.ui.theme.onPrimaryContainerLight
import cz.filip.fridgetracker_001.ui.theme.primaryLight
import io.iamjosephmj.flinger.configs.FlingConfiguration
import io.iamjosephmj.flinger.flings.flingBehavior

@Composable
fun MujTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    maxLines: Int = Int.MAX_VALUE,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    var internalIsError by remember { mutableStateOf(isError) }

    Column(modifier = modifier) {
        TextField(
            enabled = enabled,
            value = value,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                    internalIsError = false
                } else {
                    internalIsError = true
                }
            },
            maxLines = maxLines,
            placeholder = { Text(placeholder,color = Color.Black) },
            isError = internalIsError,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth(),
            colors = colors,
        )
        if (internalIsError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MojeIconVyber(
    iconList: List<Int>,
    skladIcona: Int,
    onIconSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    preferovane: Boolean,
) {
    val density = LocalDensity.current
    val gridState = rememberLazyGridState()

    val scrollProgress by remember {
        derivedStateOf {
            val totalItems = iconList.size
            if (totalItems > 0) {
                val totalColumns = (totalItems + 1) / 2
                val itemWidthPx = with(density) { 80.dp.toPx() }
                val totalScrollableWidth = totalColumns * itemWidthPx - gridState.layoutInfo.viewportEndOffset
                val scrolledWidth = gridState.firstVisibleItemIndex * itemWidthPx + gridState.firstVisibleItemScrollOffset
                (scrolledWidth / totalScrollableWidth).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = scrollProgress,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = ""
    )

    val customFlingBehavior = flingBehavior(
        scrollConfiguration = FlingConfiguration.Builder()
            .build()
    )

    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .padding(2.dp),
            color = Color.Blue,
            trackColor = Color.White,
            strokeCap = StrokeCap.Round,
        )
        LazyHorizontalGrid(
            modifier = Modifier.height(240.dp),
            state = gridState,
            rows = GridCells.Fixed(3),
            flingBehavior = customFlingBehavior,
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(iconList) { icon ->
                Box(
                    modifier = Modifier
                        .clickable { onIconSelect(icon) }
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 4.dp,
                            color = if (skladIcona == icon && !preferovane) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(icon)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MojeIconVyber2(
    iconList: List<SkladIcon>,
    skladIcona: SkladIcon,
    onIconSelect: (SkladIcon) -> Unit,
    modifier: Modifier = Modifier,
    preferovane: Boolean,
) {
    // Rozdělíme seznam ikon na chunky po 4 prvcích.
    val chunkedIcons = iconList.chunked(4)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pro každý “řádek” ikon vykreslíme Row
        chunkedIcons.forEach { rowIcons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Každá ikona v jednom Boxu se stejnou šířkou
                rowIcons.forEach { icon ->
                    Box(
                        modifier = Modifier
                            .weight(1f)             // rozdělí rovnoměrně místo
                            .aspectRatio(1f)        // udělá z boxu čtverec
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 4.dp,
                                color = if (skladIcona == icon && !preferovane) Color.Black
                                else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onIconSelect(icon) },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(icon.resId)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Pokud chcete vyplnit poslední řádek tak, aby měl také 4 sloupce,
                // i když rowIcons má třeba jen 2 prvky, můžete přidat “prázdné” boxy:
                // repeat(4 - rowIcons.size) {
                //     Box(
                //         modifier = Modifier
                //             .weight(1f)
                //             .aspectRatio(1f)
                //     ) { /* prázdné místo */ }
                // }
            }
        }
    }
}


@Composable
fun MojeExpirace(
    expirace: List<Int>,
    skladExpirace: Int,
    onExpiraceChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        expirace.forEach { option ->
            Button(
                onClick = { onExpiraceChange(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (skladExpirace == option) primaryLight else onPrimaryContainerLight
                )
            ) {
                Text(
                    text = option.toString(),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SkladFormBoxWithConstraints(
    sklad: SkladEntity,
    onSkladChange: (SkladEntity) -> Unit,
    maxPoradi: Int,
    isEdit: Boolean,
    onCameraPhotoDelete: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit = {},
) {
    // Pro kontrolu, jestli je input chybný (ukázka pro TextField):
    val isError = false
    val minPos = 1
    val maxPosLocal = if (isEdit) maxPoradi else (maxPoradi + 1)
    var deleteAlert by remember { mutableStateOf(false) }

    // Scaffold s topBar + bottomBar:
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SkladFormTopBar(
                onCancel = onCancel,
                onDelete = onDelete,
                isEdit = isEdit
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
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
                        onConfirm()
                    },
                    colors = ButtonDefaults.buttonColors(buttonPodtvrdit)
                ) {
                    Text(
                        text = stringResource(if (isEdit) R.string.save_changes else R.string.add_storage),
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                }
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            val totalHeight = this.maxHeight

            // Vztažné "váhy" pro layout
            val row1Weight = 0.5f
            val row2Weight = 0.5f
            val row3Weight = 1.5f
            val row4Weight = 1f
            val sumWeights = row1Weight + row2Weight + row3Weight + row4Weight
            fun rowHeight(weight: Float) = totalHeight * (weight / sumWeights)

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                // -------------- Řádek 1: posun pořadí --------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight(row1Weight)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                val newValue = (sklad.poradi - 1).coerceAtLeast(minPos)
                                onSkladChange(sklad.copy(poradi = newValue))
                            },
                            colors = ButtonDefaults.buttonColors(cardGradient3),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("-", fontSize = 25.sp, color = Color.White)
                        }

                        Spacer(Modifier.width(20.dp))

                        Text(
                            text = stringResource(R.string.order_format, sklad.poradi),
                            fontSize = 25.sp,
                            color = Color.Black
                        )

                        Spacer(Modifier.width(20.dp))

                        Button(
                            onClick = {
                                val newValue = (sklad.poradi + 1).coerceAtMost(maxPosLocal)
                                onSkladChange(sklad.copy(poradi = newValue))
                            },
                            colors = ButtonDefaults.buttonColors(cardGradient3),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+", fontSize = 25.sp, color = Color.White)
                        }
                    }
                }


                // -------------- Řádek 2: Název skladu --------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight(row2Weight))
                ) {
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current

                    MujTextField(
                        value = sklad.nazev,
                        onValueChange = { onSkladChange(sklad.copy(nazev = it)) },
                        placeholder = stringResource(R.string.storage_name_placeholder),
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
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = cardGradient22,
                            unfocusedContainerColor = cardGradient22,
                            disabledContainerColor = cardGradient22,
                            errorContainerColor = cardGradient22,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Red
                        )
                    )
                }

                // -------------- Řádek 3: Výběr ikony --------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight(row3Weight))
                ) {
                    val allIcons = SkladIcon.entries.toList()

                    Column {
                        Text(stringResource(R.string.pick_icon))
                        MojeIconVyber2(
                            iconList = allIcons,
                            skladIcona = sklad.icon,
                            onIconSelect = { picked ->
                                onSkladChange(
                                    sklad.copy(
                                        icon = picked,
                                        preferovane = false
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            preferovane = sklad.preferovane
                        )
                    }
                }

                // -------------- Řádek 4: Vyfocená fotka + mazání --------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(cardGradient22)
                        .height(rowHeight(row4Weight))
                        .border(2.dp, Color.Black, shape = MaterialTheme.shapes.small)
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Column(
                            modifier = Modifier.weight(0.8f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(R.drawable.camera)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onTakePhotoClick() }
                                    .size(80.dp)
                            )

                            // Tlačítko "Smazat fotku"
                            if (sklad.iconPath != null) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    onClick = { deleteAlert = true },
                                    modifier = Modifier
                                        .wrapContentWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.delete_photo),
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (deleteAlert) {
                                AlertDialog(
                                    onDismissRequest = { deleteAlert = false },
                                    title = { Text(stringResource(R.string.delete_photo_q)) },
                                    text = { Text(stringResource(R.string.irreversible)) },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                onCameraPhotoDelete()
                                                deleteAlert = false
                                            }
                                        ) {
                                            Text(
                                                text = stringResource(R.string.delete_photo),
                                                color = Color.Red,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = { deleteAlert = false }
                                        ) {
                                            Text(stringResource(R.string.cancel))
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Náhled fotky
                            sklad.iconPath?.let { path ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(path)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Vyfocená fotka",
                                    modifier = Modifier
                                        .heightIn(max = 170.dp)
                                        .aspectRatio(0.8f)
                                        .clickable { onSkladChange(sklad.copy(preferovane = true)) }
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = 4.dp,
                                            color = if (sklad.preferovane) Color.Black else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentScale = ContentScale.Fit
                                )
                            } ?: run {
                                Text(stringResource(R.string.take_your_icon), color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}