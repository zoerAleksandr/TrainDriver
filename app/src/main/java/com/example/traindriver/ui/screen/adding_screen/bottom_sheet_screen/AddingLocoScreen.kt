package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.ui.screen.adding_screen.custom_tab.CustomTab
import com.example.traindriver.ui.theme.Typography

@Composable
fun AddingLocoScreen(
    locomotive: Locomotive? = null,
    addingLocomotive: (Locomotive) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
    ) {
        val (buttonSave, editSeries, editNumber, typoLoco) = createRefs()
        var number by remember { mutableStateOf(TextFieldValue(locomotive?.number ?: "")) }
        var series by remember { mutableStateOf(TextFieldValue(locomotive?.series ?: "")) }

        Text(
            modifier = Modifier
                .constrainAs(buttonSave) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .clickable {
                    addingLocomotive.invoke(Locomotive(series = "ВЛ15", number = "033"))
                }
                .padding(end = 32.dp, top = 16.dp),
            text = "Сохранить",
            style = Typography.button.copy(color = MaterialTheme.colors.secondaryVariant))

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(editSeries) {
                    start.linkTo(parent.start)
                    end.linkTo(editNumber.start)
                    top.linkTo(buttonSave.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 32.dp, start = 16.dp, end = 8.dp),
            value = series,
            label = {
                Text(text = "Серия", style = Typography.body1)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.primary
            ),
            onValueChange = { series = it }
        )

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(editNumber) {
                    start.linkTo(editSeries.end)
                    end.linkTo(parent.end)
                    top.linkTo(buttonSave.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 32.dp, end = 16.dp, start = 8.dp),
            value = number,
            label = {
                Text("Номер", style = Typography.body1)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.primary,
            ),
            onValueChange = { number = it }
        )

        val (selected, setSelected) = remember {
            mutableStateOf(0)
        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        CustomTab(
            modifier = Modifier
                .constrainAs(typoLoco) {
                    top.linkTo(editNumber.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(16.dp),
            items = listOf("Тепловоз", "Электровоз"),
            tabWidth = (screenWidth - 32.dp) / 2,
            selectedItemIndex = selected,
            onClick = setSelected,
        )

    }
}
