package com.r00li.xperiapotatocontrol.ui.main

import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerType

import com.r00li.xperiapotatocontrol.ui.theme.XperiaPotatoControlTheme
import com.r00li.xperiapotatocontrol.R
import com.r00li.xperiapotatocontrol.ui.main.model.MainScreenState
import com.r00li.xperiapotatocontrol.ui.main.model.UiAction
import io.mhssn.colorpicker.ext.toHex

@Composable
internal fun MainScreen(viewModel: MainViewModel = MainViewModel()) {
    val state by viewModel.uiStateFlow.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {
        when (state) {
            MainScreenState.Connecting -> LoadingState()
            is MainScreenState.Connected -> ConnectedState(
                state as MainScreenState.Connected,
                onInputTextChanged = viewModel::onInputTextChanged,
                onActionClicked = viewModel::onUiAction,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(Modifier.weight(1f))
        CircularImage(drawable = R.drawable.hello_image, size = 100.dp)
        Spacer(Modifier.weight(1f))
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Connecting ...",
        )
        Spacer(Modifier.weight(1f))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ConnectedState(
    state: MainScreenState.Connected,
    onInputTextChanged: (String) -> Unit,
    onActionClicked: (UiAction) -> Unit,
    viewModel: MainViewModel?,
    modifier: Modifier = Modifier
) {
    var scrollEnabled by remember {
        mutableStateOf(true)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(), enabled = scrollEnabled)
    ) {
        titleCard()

        rowCard(closedTitle = "Eye LED controls", icon = R.drawable.hello_eye) {
            Column {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Left", style = MaterialTheme.typography.caption)
                        eyeSwitch(leftSide = true, position = 5, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = true, position = 4, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = true, position = 3, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = true, position = 2, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = true, position = 1, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Right", style = MaterialTheme.typography.caption)
                        eyeSwitch(leftSide = false, position = 5, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = false, position = 4, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = false, position = 3, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = false, position = 2, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                        eyeSwitch(leftSide = false, position = 1, onValueChange = { leftSide, position, isOn -> viewModel?.onEyeSwitchChange(leftSide, position, isOn) })
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        rowCard(closedTitle = "Neck LED controls", icon = R.drawable.hello_neck) {
            Row(modifier = Modifier.clickable(enabled = false) {}) {
                Spacer(modifier = Modifier.weight(1f))
                ColorPicker(
                    type = ColorPickerType.Circle(
                        showBrightnessBar = true,
                        showAlphaBar = false,
                        lightCenter = true
                    ),
                    modifier = Modifier.motionEventSpy {
                        scrollEnabled = it.action != MotionEvent.ACTION_DOWN
                    }
                ) {
                    viewModel?.onNeckColorChange(it.toHex(includeAlpha = false))
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        rowCard(closedTitle = "Pan and tilt controls", icon = R.drawable.hello_tilt) {
            Text("Tilt:", style = MaterialTheme.typography.body2)
            sliderControl(
                range = -8f..8f,
                numberOfTicks = 15,
                leftLabel = "Down",
                rightLabel = "Up",
                onValueChange = { viewModel?.onTiltControlChange(it) }
            )

            Spacer(modifier = Modifier.height(15.dp))
            Divider()
            Spacer(modifier = Modifier.height(15.dp))

            Text("Pan:", style = MaterialTheme.typography.body2)
            sliderControl(
                range = -10f..10f,
                numberOfTicks = 19,
                leftLabel = "Left",
                rightLabel = "Right",
                onValueChange = { viewModel?.onPanControlChange(it) }
            )
        }
        rowCard(closedTitle = "Body controls", icon = R.drawable.hello_body) {
            sliderControl(
                range = -24f..24f,
                numberOfTicks = 47,
                leftLabel = "Left",
                rightLabel = "Right",
                onValueChange = { viewModel?.onBodyControlChange(it) }
            )
        }
        rowCard(closedTitle = "Experimental", icon = R.drawable.hello_image) {
            Row(horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    modifier = Modifier.weight(3f),
                    value = state.input,
                    onValueChange = { onInputTextChanged(it) },
                    label = { Text("Value to send") })
                Spacer(Modifier.width(10.dp))
                Button(modifier = Modifier.weight(1f), onClick = {
                    onActionClicked(UiAction.SendClicked)
                }) {
                    Text(text = "Send")
                }
            }
        }
    }
}

@Composable
fun CircularImage(@DrawableRes drawable: Int, size: Dp) {
    val horizontalGradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colors.primary,
            MaterialTheme.colors.primaryVariant
        )
    )
    Image(painter = painterResource(id = drawable),
        "Hello image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .border(
                //BorderStroke(4.dp, horizontalGradientBrush),
                BorderStroke(4.dp, MaterialTheme.colors.primary),
                CircleShape
            )
            .shadow(15.dp, shape = CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
    )
}

@Composable
fun rowCard(closedTitle: String, @DrawableRes icon: Int, content: @Composable() () -> Unit) {
    val expanded = remember { mutableStateOf(false) }

    Card(shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable { expanded.value = !expanded.value }
    ) {
        //Box(modifier = Modifier.background(brush = horizontalGradientBrush)) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularImage(drawable = icon, size = 60.dp)
                Spacer(modifier = Modifier.width(20.dp))
                Text(closedTitle, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                if (expanded.value) {
                    Icon(painterResource(id = R.drawable.expand_more_40px), "expand more")
                } else {
                    Icon(painterResource(id = R.drawable.expand_less_40px), "expand less")
                }
            }
            if (expanded.value) {
                Spacer(Modifier.height(10.dp))
                content()
            }
            /*Row {
        }*/
        }
        // }

    }
}

@Composable
fun titleCard() {
    val horizontalGradientBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colors.primary,
            MaterialTheme.colors.primaryVariant
        )
    )

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Box(modifier = Modifier.background(brush = horizontalGradientBrush)) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularImage(drawable = R.drawable.hello_image, size = 60.dp)
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text("Xperia Hello", fontWeight = FontWeight.Bold)
                        Text("Connected", style = MaterialTheme.typography.body2)
                    }
                }
            }
        }
    }
}

@Composable
fun sliderControl(range: ClosedFloatingPointRange<Float>, numberOfTicks: Int, leftLabel: String, rightLabel: String, onValueChange: (Float) -> Unit) {
    var sliderValue by remember { mutableStateOf(0f) }
    Slider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        onValueChangeFinished = { onValueChange(sliderValue) },
        valueRange = range,
        steps = numberOfTicks,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.primary,
            activeTrackColor = MaterialTheme.colors.primary,
            inactiveTrackColor = MaterialTheme.colors.primary,
            activeTickColor = MaterialTheme.colors.onPrimary,
            inactiveTickColor = MaterialTheme.colors.onPrimary
        )
    )
    Row {
        Text(leftLabel, style = MaterialTheme.typography.caption)
        Spacer(Modifier.weight(1f))
        Text(sliderValue.toInt().toString())
        Spacer(Modifier.weight(1f))
        Text(rightLabel, style = MaterialTheme.typography.caption)
    }
}

@Composable
fun eyeSwitch(leftSide: Boolean, position: Int, onValueChange: (Boolean, Int, Boolean) -> Unit) {
    var isOn by remember { mutableStateOf(false) }
    Switch(
        checked = isOn,
        onCheckedChange = { isOn = it; onValueChange(leftSide, position, it) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color.White,
            uncheckedThumbColor = Color.LightGray,
            uncheckedTrackColor = Color.White,
            checkedTrackAlpha = 0.8f
        )
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingStatePreview() {
    XperiaPotatoControlTheme {
        LoadingState()
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectedStatePreview() {
    XperiaPotatoControlTheme {
        ConnectedState(
            MainScreenState.Connected(""),
            {},
            {},
            null
        )
    }
}
