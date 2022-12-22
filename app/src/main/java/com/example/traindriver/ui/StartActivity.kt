package com.example.traindriver.ui

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.ui.screen.SetupNavGraph
import com.example.traindriver.ui.screen.splash_screen.SplashViewModel
import com.example.traindriver.ui.theme.TrainDriverTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        val viewModel: SplashViewModel by viewModel()
        val screen by viewModel.startDestination

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            TrainDriverTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    startDestination = screen,
                    activity = this
                )
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}