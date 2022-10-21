package com.example.traindriver.ui.signin_screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleState

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainDriverTheme {
                SignInScreen()
            }
        }
    }
}

@Composable
private fun SignInScreen() {
    Background()
    StartElements(localeState = LocaleState.OTHER)
//    LoadingElement()
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        SignInScreen()
    }
}