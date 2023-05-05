package com.example.traindriver.ui.screen.adding_screen.adding_loco

sealed class BottomSheetLoco{
    object CoefficientSheet: BottomSheetLoco()
    object RefuelSheet: BottomSheetLoco()
}
