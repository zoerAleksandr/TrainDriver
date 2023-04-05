package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen

import com.example.traindriver.domain.entity.Locomotive

sealed class BottomSheetScreen{
    object AddingLoco: BottomSheetScreen() {
        var locomotive: Locomotive? = null
    }
    object AddingTrain: BottomSheetScreen()
    object AddingPass: BottomSheetScreen()
}