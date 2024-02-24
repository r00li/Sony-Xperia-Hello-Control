package com.r00li.xperiapotatocontrol.ui.main.model

sealed interface MainScreenState {

    object Connecting : MainScreenState
    data class Connected(val input: String) : MainScreenState

}
