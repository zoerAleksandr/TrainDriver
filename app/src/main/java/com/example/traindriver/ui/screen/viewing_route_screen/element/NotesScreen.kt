package com.example.traindriver.ui.screen.viewing_route_screen.element

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Notes
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.Constants
import com.maxkeppeker.sheets.core.views.Grid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NotesScreen(response: RouteResponse, navController: NavController) {
    Crossfade(
        targetState = response,
        animationSpec = tween(durationMillis = Constants.DURATION_CROSSFADE)
    ) { state ->
        when (state) {
            is ResultState.Loading -> {
                LoadingScreen()
            }
            is ResultState.Success -> state.data?.let { route ->
                DataScreen(route.notes, navController)
            }
            is ResultState.Failure -> {
                FailureScreen()
            }
        }
    }
}

@Composable
private fun DataScreen(notes: Notes?, navController: NavController) {
    val scrollState = rememberLazyListState()
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (shadow, data) = createRefs()

        AnimatedVisibility(
            modifier = Modifier.zIndex(1f),
            visible = !scrollState.isScrollInInitialState(),
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            BottomShadow(modifier = Modifier.constrainAs(shadow) { top.linkTo(parent.top) })
        }
        if (notes == null || (notes.text.isNullOrBlank() && notes.photos.isEmpty())) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Нет данных", style = Typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(data) {
                        top.linkTo(parent.top)
                    },
                state = scrollState
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        text = notes.text ?: "",
                        style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                    )
                }
                item {
                    Column {
                        Grid(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            items = notes.photos,
                            columns = 2,
                            rowSpacing = 12.dp,
                            columnSpacing = 12.dp,
                        ) { item ->
                            ItemPhoto(item, navController)
                        }

                    }
                }
                item { Spacer(modifier = Modifier.padding(24.dp)) }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    LoadingElement()
}

@Composable
private fun FailureScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.route_opening_error),
            style = Typography.displaySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPhoto(
    photo: Uri,
    navController: NavController
) {
    val widthScreen = LocalConfiguration.current.screenWidthDp
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height((widthScreen / 2).dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = ShapeBackground.extraSmall
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        onClick = {
            scope.launch {
                val encodeUri =
                    withContext(Dispatchers.IO) {
                        URLEncoder.encode(
                            photo.toString(),
                            StandardCharsets.UTF_8.toString()
                        )
                    }
                navController.navigate(
                    Screen.ViewingPhoto.openPhoto(
                        photo = encodeUri,
                        tag = Screen.ViewingPhotoTag.ONLY_VIEW
                    )
                )
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(photo),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
        }
    )
}