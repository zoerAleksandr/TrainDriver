package com.example.traindriver.ui.screen.adding_screen.adding_notes

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel
import com.example.traindriver.ui.screen.photo.EMPTY_IMAGE_URI
import com.example.traindriver.ui.screen.viewing_route_screen.element.BottomShadow
import com.example.traindriver.ui.screen.viewing_route_screen.element.isScrollInInitialState
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.maxkeppeker.sheets.core.views.Grid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun AddingNotesScreen(
    navController: NavController,
    notesId: String?,
    addingRouteViewModel: AddingViewModel,
    addingNotesViewModel: AddingNotesViewModel
) {
    val widthScreen = LocalConfiguration.current.screenWidthDp

    @Composable
    fun ItemPhoto(
        photo: Uri,
    ) {
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
            content = {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(photo),
                    contentDescription = null
                )
            },
            onClick = {
                scope.launch {
                    // TODO navigate to viewing photo
                }
            }
        )
    }

    @Composable
    fun ItemPreview() {
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
                navController.navigate(Screen.CreatePhoto.route)
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CameraCapturePreview()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.7f))
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(12.dp),
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Добавить фото"
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Добавить фото",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    val scope = rememberCoroutineScope()

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                addingNotesViewModel.setData(
                    notes = addingRouteViewModel.stateNotes.value
                )
            }
            else -> {}
        }
    }

    BackHandler {
        addingNotesViewModel.clearField()
        navController.navigateUp()
    }

    Scaffold(modifier = Modifier.fillMaxWidth(), topBar = {
        MediumTopAppBar(title = {
            Text(
                text = "Примечания",
                style = Typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary)
            )
        }, navigationIcon = {
            IconButton(onClick = {
                addingNotesViewModel.clearField()
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = "Назад"
                )
            }
        }, actions = {
            ClickableText(modifier = Modifier.padding(end = 10.dp),
                text = AnnotatedString(text = "Сохранить"),
                style = Typography.titleMedium,
                onClick = {
                    addingNotesViewModel.addNotesInRoute(
                        addingRouteViewModel.stateNotes
                    )
                    navController.navigateUp()
                })

            var dropDownExpanded by remember { mutableStateOf(false) }

            IconButton(onClick = {
                dropDownExpanded = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = "Меню"
                )
                DropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false },
                    offset = DpOffset(x = 4.dp, y = 8.dp)
                ) {
                    DropdownMenuItem(modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            addingNotesViewModel.clearField()
                            dropDownExpanded = false
                        },
                        text = {
                            Text(
                                text = "Очистить",
                                style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        })
                }
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        )
        )
    }) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            val (topShadow, content) = createRefs()
            val scrollState = rememberLazyListState()

            val formState = addingNotesViewModel.formState
            val notesText = formState.notesText.data

            AnimatedVisibility(
                modifier = Modifier
                    .zIndex(1f)
                    .constrainAs(topShadow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                visible = !scrollState.isScrollInInitialState(),
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                BottomShadow()
            }

            LazyColumn(
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                state = scrollState
            ) {
                item {
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        value = notesText ?: "",
                        onValueChange = {
                            addingNotesViewModel.createEventNotes(
                                NotesEvent.EnteredNotesText(it)
                            )
                        },
                        textStyle = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                        placeholder = {
                            Text(
                                text = "Здесь можно добавить заметки",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            scope.launch {
                                focusManager.clearFocus()
                            }
                        })
                    )
                }
                item {
                    val list = addingNotesViewModel.photosList
                    Grid(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        items = list,
                        columns = 2,
                        rowSpacing = 12.dp,
                        columnSpacing = 12.dp,
                    ) { item ->
                        if (list.isEmpty() || item == EMPTY_IMAGE_URI) {
                            ItemPreview()
                        } else {
                            ItemPhoto(item)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.padding(24.dp)) }
            }
        }
    }
}