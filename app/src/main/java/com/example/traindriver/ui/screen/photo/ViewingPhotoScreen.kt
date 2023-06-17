package com.example.traindriver.ui.screen.photo

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.adding_screen.adding_notes.AddingNotesViewModel
import com.example.traindriver.ui.screen.adding_screen.adding_notes.NotesEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewingPhotoScreen(
    navController: NavController,
    addingNotesViewModel: AddingNotesViewModel,
    photo: String,
    tag: Screen.ViewingPhotoTag
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (tag == Screen.ViewingPhotoTag.CHANGEABLE) {
                        IconButton(
                            onClick = {
                                addingNotesViewModel.createEventNotes(
                                    NotesEvent.RemovePhoto(
                                        Uri.parse(
                                            photo
                                        )
                                    )
                                )
                                navController.navigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Удалить"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    )
    {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(photo),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )
        }
    }
}