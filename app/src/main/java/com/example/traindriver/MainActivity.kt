package com.example.traindriver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.traindriver.ui.signin_screen.Background
import com.example.traindriver.ui.signin_screen.LoadingElement
import com.example.traindriver.ui.signin_screen.StartElements
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainDriverTheme {
                Background()
                StartElements()
            }
        }
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        Background()
        StartElements()
        LoadingElement()
    }
}