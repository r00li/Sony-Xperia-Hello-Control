package com.r00li.xperiapotatocontrol.ui.main.model

sealed interface UiAction {
    object SendClicked : UiAction
    object UpClicked : UiAction
    object DownClicked : UiAction
    object LeftClicked : UiAction
    object RightClicked : UiAction
    object RotateLeftClicked : UiAction
    object RotateRightClicked : UiAction
}
