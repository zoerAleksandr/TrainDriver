package com.example.traindriver.ui.screen.photo

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.adding_screen.adding_notes.AddingNotesViewModel
import com.example.traindriver.ui.screen.adding_screen.adding_notes.NotesEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
    val scope = rememberCoroutineScope()
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
                    scope.launch {
                        val encodeUri =
                            withContext(Dispatchers.IO) {
                                URLEncoder.encode(
                                    file.toUri().toString(),
                                    StandardCharsets.UTF_8.toString()
                                )
                            }
                        navController.navigate(Screen.PreviewPhoto.setPhoto(encodeUri))
                    }
                }
            )
        }
    }
}