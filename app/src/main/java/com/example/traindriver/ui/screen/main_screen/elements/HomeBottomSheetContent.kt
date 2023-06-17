package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.signin_screen.elements.PrimarySpacer
import com.example.traindriver.ui.util.SnackbarMessage.DATA_LOADING_ERROR
import com.example.traindriver.ui.util.changeDpWithScroll
import kotlinx.coroutines.launch

@Composable
fun HomeBottomSheetContent(
    listRoute: ResultState<List<Route>>,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    contentHeight: Dp,
    offset: Float
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(contentHeight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val paddingTop = changeDpWithScroll(offset, 32, 0)
        LazyColumn(
            modifier = Modifier.padding(top = paddingTop)
        ) {
            when (listRoute) {
                is ResultState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.size(70.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }
                }
                is ResultState.Success -> listRoute.data?.let { list ->
                    if (list.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.empty_route_list),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        itemsIndexed(list) {index, route ->
                            ItemMainScreen(route = route) {
                                navController
                                    .navigate(Screen.ViewingRoute.passId(route.id))
                            }
                            if (index == list.lastIndex){
                                PrimarySpacer()
                            }
                        }
                    }
                }
                is ResultState.Failure -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(DATA_LOADING_ERROR)
                    }
                }
            }
        }
    }
}