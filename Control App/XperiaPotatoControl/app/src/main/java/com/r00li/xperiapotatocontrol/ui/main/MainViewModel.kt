package com.r00li.xperiapotatocontrol.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r00li.xperiapotatocontrol.data.XperiaSerialManager
import com.r00li.xperiapotatocontrol.ui.main.model.MainScreenState
import com.r00li.xperiapotatocontrol.ui.main.model.UiAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.BitSet

internal class MainViewModel(private val serialManager: XperiaSerialManager = XperiaSerialManager()) :
    ViewModel() {

    private val _uiFlow = MutableStateFlow<MainScreenState>(MainScreenState.Connecting)
    val uiStateFlow: StateFlow<MainScreenState> = _uiFlow.asStateFlow()

    init {
        viewModelScope.launch {
            serialManager.connect()
                .collect {
                    when (it) {
                        is XperiaSerialManager.Result.DataReceived -> {
                            println("Data received: ${it.data}")
                            // TODO Do something with received data
                        }

                        XperiaSerialManager.Result.Opened -> {
                            // Add artificial delay to show loading state
                            delay(2000)
                            _uiFlow.emit(
                                MainScreenState.Connected(
                                    ""
                                )
                            )
                        }
                    }
                }
        }
    }

    fun onUiAction(action: UiAction) {
        when (action) {
            UiAction.SendClicked -> sendCurrentInput()
            UiAction.UpClicked,
            UiAction.DownClicked,
            UiAction.LeftClicked,
            UiAction.RightClicked,
            UiAction.RotateLeftClicked,
            UiAction.RotateRightClicked -> updatePosition(action)
        }
    }

    fun onInputTextChanged(input: String) {
        viewModelScope.launch {
            val currentState = (_uiFlow.value as MainScreenState.Connected)
            _uiFlow.emit(currentState.copy(input = input))
        }
    }

    fun onTiltControlChange(input: Float) {
        viewModelScope.launch {
            val newValue = (input.toInt() * 1280).toShort()
            serialManager.updateTiltPosition(newValue)
        }
    }

    fun onPanControlChange(input: Float) {
        viewModelScope.launch {
            val newValue = (input.toInt() * 1280).toShort()
            serialManager.updatePanPosition(newValue)
        }
    }

    fun onBodyControlChange(input: Float) {
        viewModelScope.launch {
            val newValue = (input.toInt() * 1280).toShort()
            serialManager.updateRotationPosition(newValue)
        }
    }

    fun onEyeSwitchChange(leftSide: Boolean, position: Int, isOn: Boolean) {
        viewModelScope.launch {
            if (leftSide) {
                var newSet = serialManager.letfBitset
                newSet.set(position - 1, isOn)
                serialManager.updateEyeLeds(newSet, serialManager.rightBitSet)
            } else {
                var newSet = serialManager.rightBitSet
                newSet.set(position - 1, isOn)
                serialManager.updateEyeLeds(serialManager.letfBitset, newSet)
            }
        }
    }

    fun onNeckColorChange(newValue: String) {
        viewModelScope.launch {
            serialManager.updateNeckColor(newValue.uppercase())
        }
    }

    private fun updatePosition(action: UiAction) {
        viewModelScope.launch {
            when (action) {
                UiAction.UpClicked -> serialManager.updateTiltPosition((serialManager.tiltPosition + 1280).toShort())
                UiAction.DownClicked -> serialManager.updateTiltPosition((serialManager.tiltPosition - 1280).toShort())
                UiAction.LeftClicked -> serialManager.updatePanPosition((serialManager.panPosition - 1280).toShort())
                UiAction.RightClicked -> serialManager.updatePanPosition((serialManager.panPosition + 1280).toShort())
                UiAction.RotateLeftClicked -> serialManager.updateRotationPosition((serialManager.bodyPosition - 1280).toShort())
                UiAction.RotateRightClicked -> serialManager.updateRotationPosition((serialManager.bodyPosition + 1280).toShort())
                else -> throw IllegalStateException("Unsupported action!")
            }
        }
    }

    private fun sendCurrentInput() {
        // TODO if user can press send then we should be in Connected state, so can probably force cast this
        (_uiFlow.value as? MainScreenState.Connected)?.let {
            val toSend = (it.input + "\r\n").toByteArray(Charsets.UTF_8)
            viewModelScope.launch {
                serialManager.sendData(toSend)
            }
        }
    }
}
