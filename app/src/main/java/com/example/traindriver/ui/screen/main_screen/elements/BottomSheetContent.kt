package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.element_screen.HandleBottomSheet
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.util.SnackbarMessage.DATA_LOADING_ERROR
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent(
    listRoute: ResultState<List<Route>>,
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HandleBottomSheet()
        LazyColumn(modifier = Modifier.padding(top = 32.dp)) {
            when (listRoute) {
                is ResultState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.size(70.dp),
                            color = MaterialTheme.colors.primary,
                            strokeWidth = 3.dp
                        )
                    }
                }
                is ResultState.Success -> listRoute.data?.let { list ->
                    if (list.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.empty_route_list),
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                    } else {
                        items(list) { route ->
                            ItemMainScreen(route = route) {
                                navController
                                    .navigate(Screen.ViewingRoute.passId(route.id))
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