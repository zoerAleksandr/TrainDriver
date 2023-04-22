package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import com.example.traindriver.R
import com.example.traindriver.domain.entity.Calculation
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.double_util.*
import com.example.traindriver.ui.util.float_util.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun maskInKilo(string: String?): String? {
    return string?.let {
        "$it кг"
    }
}

fun maskInLiter(string: String?): String? {
    return string?.let {
        "$it л"
    }
}

@Composable
fun DieselSectionItem(
    index: Int,
    item: SectionType.DieselSectionFormState,
    viewModel: AddingLocoViewModel,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>,
    openSheet: (BottomSheetLoco) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val acceptedText = item.accepted.data ?: ""
    val accepted = acceptedText.toDoubleOrNull()
    val deliveryText = item.delivery.data ?: ""
    val delivery = deliveryText.toDoubleOrNull()
    val coefficient = item.coefficient.data?.toDoubleOrNull()
    val refuel = item.refuel.data?.toDoubleOrNull()
    val acceptedInKilo = accepted.times(coefficient)
    val deliveryInKilo = delivery.times(coefficient)
    val result = Calculation.getTotalFuelConsumption(accepted, delivery, refuel)
    val resultInKilo = Calculation.getTotalFuelInKiloConsumption(result, coefficient)
    val formValid = viewModel.dieselSectionListState[index].formValid
    val errorMessageText = viewModel.dieselSectionListState[index].errorMessage
    ConstraintLayout {
        val (sectionNum, refuelButton,
            energyAccepted, energyDelivery,
            inKiloBlock, infoBlock, errorMessage) = createRefs()

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(errorMessage) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)

                },
            visible = !formValid,
            enter = slideInVertically(animationSpec = tween(durationMillis = 300))
                    + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = slideOutVertically(animationSpec = tween(durationMillis = 300))
                    + fadeOut(animationSpec = tween(durationMillis = 150))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error_24),
                    tint = Color.Red,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = errorMessageText,
                    style = Typography.bodySmall.copy(color = Color.Red),
                    color = Color.Red
                )
            }
        }

        Text(
            modifier = Modifier
                .constrainAs(sectionNum) {
                    top.linkTo(errorMessage.bottom)
                    start.linkTo(parent.start)
                }
                .padding(top = 16.dp, start = 16.dp),
            text = "${index + 1} секция",
            style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
        )

        Row(modifier = Modifier
            .constrainAs(refuelButton) {
                top.linkTo(errorMessage.bottom)
                end.linkTo(parent.end)
            }
            .clickable {
                refuelState.value = refuelState.value.copy(
                    first = index, second = refuel?.str() ?: ""
                )
                scope.launch {
                    openSheet.invoke(BottomSheetLoco.RefuelSheet)
                }
            }
            .padding(top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically)
        {
            refuel?.let {
                Text(
                    text = maskInLiter(it.str()) ?: "",
                    style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                )
            }
            Image(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.icon_size))
                    .padding(8.dp),
                painter = painterResource(id = R.drawable.refuel_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(energyAccepted) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(energyDelivery.start, 8.dp)
                    top.linkTo(refuelButton.bottom, 8.dp)
                    width = Dimension.fillToConstraints
                },
            value = acceptedText,
            onValueChange = {
                viewModel.createEventDieselSection(
                    DieselSectionEvent.EnteredAccepted(
                        index = index, data = it
                    )
                )
                viewModel.createEventDieselSection(
                    DieselSectionEvent.FocusChange(
                        index = index,
                        fieldName = DieselSectionType.ACCEPTED
                    )
                )
            },
            placeholder = {
                Text(text = "Принято", color = MaterialTheme.colorScheme.secondary)
            },
            textStyle = Typography.bodyLarge
                .copy(color = MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                scope.launch {
                    focusManager.moveFocus(FocusDirection.Right)
                }
            })
        )

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(energyDelivery) {
                    start.linkTo(energyAccepted.end, 8.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(refuelButton.bottom, 8.dp)
                    width = Dimension.fillToConstraints
                },
            value = deliveryText,
            onValueChange = {
                viewModel.createEventDieselSection(
                    DieselSectionEvent.EnteredDelivery(
                        index = index, data = it
                    )
                )
                viewModel.createEventDieselSection(
                    DieselSectionEvent.FocusChange(
                        index = index,
                        fieldName = DieselSectionType.DELIVERY
                    )
                )
            },
            placeholder = {
                Text(text = "Сдано", color = MaterialTheme.colorScheme.secondary)
            },
            textStyle = Typography.bodyLarge
                .copy(color = MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                scope.launch {
                    focusManager.clearFocus()
                }
            })
        )

        val visibleInfoInKiloState =
            (!item.accepted.data.isNullOrBlank() || !item.delivery.data.isNullOrBlank())

        Row(
            modifier = Modifier
                .constrainAs(inKiloBlock) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(energyAccepted.bottom)
                    visibility = if (visibleInfoInKiloState) Visibility.Visible else Visibility.Gone
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                val acceptedInKiloText = rounding(acceptedInKilo, 2)?.str()
                Text(
                    text = maskInKilo(acceptedInKiloText) ?: "",
                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                val deliveryInKiloText = rounding(deliveryInKilo, 2)?.str()
                Text(
                    text = maskInKilo(deliveryInKiloText) ?: "",
                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
        }

        Row(
            modifier = Modifier
                .constrainAs(infoBlock) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(inKiloBlock.bottom)
                }
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ClickableText(
                text = AnnotatedString("k = ${coefficient ?: 0.0}"),
                style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.tertiary),
                onClick = {
                    coefficientState.value = coefficientState.value.copy(
                        first = index, second = coefficient?.str() ?: ""
                    )
                    scope.launch {
                        openSheet.invoke(BottomSheetLoco.CoefficientSheet)
                    }
                })

            result?.let {
                val resultInLiterText = maskInLiter(it.str())
                val resultInKiloText = maskInKilo(rounding(resultInKilo, 2)?.str())
                Text(
                    text = "${resultInLiterText ?: ""} / ${resultInKiloText ?: ""}",
                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ElectricSectionItem(
    item: SectionType.ElectricSectionFormState,
    index: Int,
    viewModel: AddingLocoViewModel,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val acceptedText = item.accepted.data ?: ""
    val accepted = acceptedText.toDoubleOrNull()
    val deliveryText = item.delivery.data ?: ""
    val delivery = deliveryText.toDoubleOrNull()

    val recoveryAcceptedText = item.recoveryAccepted.data ?: ""
    val recoveryAccepted = recoveryAcceptedText.toDoubleOrNull()
    val recoveryDeliveryText = item.recoveryDelivery.data ?: ""
    val recoveryDelivery = recoveryDeliveryText.toDoubleOrNull()

    val result = Calculation.getTotalEnergyConsumption(accepted, delivery)
    val resultRecovery = Calculation.getTotalEnergyConsumption(recoveryAccepted, recoveryDelivery)

    val formValid = viewModel.electricSectionListState[index].formValid
    val errorMessageText = viewModel.electricSectionListState[index].errorMessage

    var expandState by remember { mutableStateOf(false) }

    ConstraintLayout {
        val (
            sectionNum, energyAccepted, buttonVisible,
            energyDelivery, recoveryBlock,
            infoBlock, errorMessage
        ) = createRefs()

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(errorMessage) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)

                },
            visible = !formValid,
            enter = slideInVertically(animationSpec = tween(durationMillis = 300))
                    + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = slideOutVertically(animationSpec = tween(durationMillis = 300))
                    + fadeOut(animationSpec = tween(durationMillis = 150))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error_24),
                    tint = Color.Red,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .basicMarquee(animationMode = MarqueeAnimationMode.WhileFocused),
                    text = errorMessageText,
                    style = Typography.bodySmall.copy(color = Color.Red),
                    maxLines = 1,
                    color = Color.Red
                )
            }
        }

        Text(modifier = Modifier
            .constrainAs(sectionNum) {
                top.linkTo(errorMessage.bottom)
                start.linkTo(parent.start)
            }
            .padding(top = 16.dp, start = 16.dp),
            text = "${index + 1} секция",
            style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary))

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(energyAccepted) {
                    start.linkTo(parent.start)
                    end.linkTo(energyDelivery.start)
                    top.linkTo(sectionNum.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
            value = acceptedText,
            onValueChange = {
                viewModel.createEventElectricSection(
                    ElectricSectionEvent.EnteredAccepted(
                        index = index, data = it
                    )
                )
                viewModel.createEventElectricSection(
                    ElectricSectionEvent.FocusChange(
                        index = index, fieldName = ElectricSectionType.ACCEPTED
                    )
                )
            },
            textStyle = Typography.bodyLarge
                .copy(color = MaterialTheme.colorScheme.primary),
            placeholder = {
                Text(text = "Принято", color = MaterialTheme.colorScheme.secondary)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                scope.launch {
                    focusManager.moveFocus(FocusDirection.Right)
                }
            })
        )

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(energyDelivery) {
                    end.linkTo(parent.end)
                    top.linkTo(sectionNum.bottom)
                    start.linkTo(energyAccepted.end)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            value = deliveryText,
            textStyle = Typography.bodyLarge
                .copy(color = MaterialTheme.colorScheme.primary),
            onValueChange = {
                viewModel.createEventElectricSection(
                    ElectricSectionEvent.EnteredDelivery(
                        index = index, data = it
                    )
                )
                viewModel.createEventElectricSection(
                    ElectricSectionEvent.FocusChange(
                        index = index, fieldName = ElectricSectionType.DELIVERY
                    )
                )
            },
            placeholder = {
                Text(text = "Сдано", color = MaterialTheme.colorScheme.secondary)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                scope.launch {
                    focusManager.clearFocus()
                }
            })
        )

        AnimatedContent(
            modifier = Modifier.constrainAs(recoveryBlock) {
                top.linkTo(energyAccepted.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            targetState = expandState
        ) { targetState ->
            if (targetState) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .weight(0.5f),
                        value = recoveryAcceptedText,
                        onValueChange = {
                            viewModel.createEventElectricSection(
                                ElectricSectionEvent.EnteredRecoveryAccepted(
                                    index = index, data = it
                                )
                            )
                            viewModel.createEventElectricSection(
                                ElectricSectionEvent.FocusChange(
                                    index = index,
                                    fieldName = ElectricSectionType.RECOVERY_ACCEPTED
                                )
                            )
                        },
                        textStyle = Typography.bodyLarge
                            .copy(color = MaterialTheme.colorScheme.primary),
                        placeholder = {
                            Text(
                                text = "Принято",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ), keyboardActions = KeyboardActions(onNext = {
                            scope.launch {
                                focusManager.moveFocus(FocusDirection.Right)
                            }
                        })
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .weight(0.5f),
                        value = recoveryDeliveryText,
                        onValueChange = {
                            viewModel.createEventElectricSection(
                                ElectricSectionEvent.EnteredRecoveryDelivery(
                                    index = index, data = it
                                )
                            )
                            viewModel.createEventElectricSection(
                                ElectricSectionEvent.FocusChange(
                                    index = index,
                                    fieldName = ElectricSectionType.RECOVERY_DELIVERY
                                )
                            )
                        },
                        textStyle = Typography.bodyLarge
                            .copy(color = MaterialTheme.colorScheme.primary),
                        placeholder = {
                            Text(text = "Сдано", color = MaterialTheme.colorScheme.secondary)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            scope.launch {
                                focusManager.clearFocus()
                            }
                        })
                    )
                }
            }
        }

        val visibilityResult = (result != null || resultRecovery != null)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(infoBlock) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(recoveryBlock.bottom)
                },
            contentAlignment = Alignment.CenterEnd
        ) {
            AnimatedVisibility(
                visible = visibilityResult,
                enter = slideInHorizontally(animationSpec = tween(durationMillis = 300))
                        + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = slideOutHorizontally(animationSpec = tween(durationMillis = 300))
                        + fadeOut(animationSpec = tween(durationMillis = 150))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    result?.let {
                        Text(text = it.str(), style = Typography.bodyLarge)
                    }
                    resultRecovery?.let {
                        Text(text = " / ${it.str()}", style = Typography.bodyLarge)
                    }

                }
            }
        }

        Row(
            modifier = Modifier
                .constrainAs(buttonVisible) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(infoBlock.bottom)
                }
                .padding(top = 8.dp)
                .clickable {
                    expandState = !expandState
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = if (expandState) {
                    painterResource(R.drawable.up_arrow)
                } else {
                    painterResource(R.drawable.down_arrow)
                },
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
        }
    }

}

const val ANIMATION_DURATION = 300
const val MIN_DRAG_AMOUNT = 6
const val CARD_OFFSET = 86f

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableItem(
    modifier: Modifier = Modifier,
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardCollapsedBackgroundColor = MaterialTheme.colorScheme.background
    val cardExpandedBackgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)

    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, "itemTransition")

    val backgroundColor by transition.animateColor(label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (isRevealed) cardExpandedBackgroundColor else cardCollapsedBackgroundColor
        })
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) -CARD_OFFSET.dp() else 0f },
    )

    Card(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .offset { IntOffset(offsetTransition.roundToInt(), 0) }
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                when {
                    dragAmount <= MIN_DRAG_AMOUNT -> onExpand()
                    dragAmount > -MIN_DRAG_AMOUNT -> onCollapse()
                }
            }
        },
        border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colorScheme.outline
        ),
        shape = ShapeBackground.extraSmall,
        colors =  CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        content = content
    )
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableElectricItem(
    modifier: Modifier = Modifier,
    item: SectionType.ElectricSectionFormState,
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    index: Int,
    viewModel: AddingLocoViewModel,
) {
    DraggableItem(modifier = modifier,
        isRevealed = isRevealed,
        onExpand = onExpand,
        onCollapse = onCollapse,
        content = {
            ElectricSectionItem(
                item = item, index = index, viewModel = viewModel
            )
        })
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableDieselItem(
    modifier: Modifier = Modifier,
    item: SectionType.DieselSectionFormState,
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    index: Int,
    viewModel: AddingLocoViewModel,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>,
    openSheet: (BottomSheetLoco) -> Unit,
) {
    DraggableItem(modifier = modifier,
        isRevealed = isRevealed,
        onExpand = onExpand,
        onCollapse = onCollapse,
        content = {
            DieselSectionItem(
                index = index,
                item = item,
                viewModel = viewModel,
                coefficientState = coefficientState,
                refuelState = refuelState,
                openSheet = openSheet
            )
        })
}

@Composable
fun ActionsRow(
    onDelete: () -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterEnd
    ) {
        IconButton(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.min_size_view))
                .background(
                    color = MaterialTheme.colorScheme.surface, shape = CircleShape
                ), onClick = onDelete
        ) {
            Image(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Red)
            )
        }
    }
}