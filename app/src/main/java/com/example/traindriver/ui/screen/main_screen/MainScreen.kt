package com.example.traindriver.ui.screen.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.ui.element_screen.HandleBottomSheet
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.changeAlphaWithScroll

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController, mainViewModel: MainViewModel = viewModel()
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scaffoldState = rememberScaffoldState()
    val offset = bottomSheetScaffoldState.bottomSheetState.offset

    BottomSheetScaffold(sheetBackgroundColor = changeAlphaWithScroll(
        offset = offset.value, initColor = MaterialTheme.colors.onSurface
    ),
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 0.dp,
        sheetPeekHeight = 260.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.97f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HandleBottomSheet()
                LazyColumn {
                    //TODO items
                }
            }
        }) {
        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(
                    hostState = it
                ) { snackBarData ->
                    TopSnackbar(snackBarData)
                }
            }
        ) {}
    }
}

@Composable
@DarkLightPreviews
private fun MainPreview() {
    TrainDriverTheme {
        MainScreen(navController = rememberNavController())
    }
}