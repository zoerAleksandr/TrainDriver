package com.example.traindriver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.traindriver.ui.theme.TrainDriverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainDriverTheme {
                // A surface container using the 'background' color from the theme

            }
        }
    }
}
