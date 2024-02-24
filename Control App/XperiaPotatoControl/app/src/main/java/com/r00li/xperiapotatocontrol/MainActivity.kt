package com.r00li.xperiapotatocontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.r00li.xperiapotatocontrol.ui.theme.XperiaPotatoControlTheme
import com.r00li.xperiapotatocontrol.ui.main.MainScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            XperiaPotatoControlTheme {
                MainScreen()
            }
        }
    }
}
