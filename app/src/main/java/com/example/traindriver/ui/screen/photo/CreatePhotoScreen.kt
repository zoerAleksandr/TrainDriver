package com.example.traindriver.ui.screen.photo

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.example.traindriver.ui.screen.adding_screen.adding_notes.AddingNotesViewModel
import com.example.traindriver.ui.screen.adding_screen.adding_notes.CameraCapture
import com.example.traindriver.ui.screen.adding_screen.adding_notes.NotesEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun CreatePhotoScreen(
    modifier: Modifier = Modifier,
    addingNotesViewModel: AddingNotesViewModel,
    navController: NavController
) {
    val showGallerySelect = remember { mutableStateOf(false) }
    if (showGallerySelect.value) {
        GallerySelect(
            modifier = modifier,
            onImageUri = { uri ->
                showGallerySelect.value = false
                addingNotesViewModel.createEventNotes(NotesEvent.AddingPhoto(uri))
                navController.popBackStack()
            }
        )
    } else {
        Box(modifier = modifier) {
            CameraCapture(
                modifier = modifier,
                gallerySelect = showGallerySelect,
                onImageFile = { file ->
                    showGallerySelect.value = false
                    addingNotesViewModel.createEventNotes(NotesEvent.AddingPhoto(file.toUri()))
                    navController.popBackStack()
                }
            )
        }
    }
}