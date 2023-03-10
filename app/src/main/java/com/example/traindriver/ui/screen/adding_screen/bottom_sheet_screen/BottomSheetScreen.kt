package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen

sealed class BottomSheetScreen{
    object AddingLoco: BottomSheetScreen()
    object AddingTrain: BottomSheetScreen()
    object AddingPass: BottomSheetScreen()
}