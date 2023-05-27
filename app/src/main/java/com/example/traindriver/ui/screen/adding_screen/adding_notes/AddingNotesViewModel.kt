package com.example.traindriver.ui.screen.adding_screen.adding_notes

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.domain.entity.Notes
import com.example.traindriver.ui.screen.photo.EMPTY_IMAGE_URI
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class AddingNotesViewModel : ViewModel() {
    private var currentNotes: Notes by Delegates.observable(
        initialValue = Notes()
    ) { _, _, notes ->
        setNotesText(TextFieldValue(notes.text ?: ""))
        setPhotos(notes.photos)
    }

    var formState by mutableStateOf(NotesFormState(id = currentNotes.id))
        private set

    var photosList = mutableStateListOf(EMPTY_IMAGE_URI)
        private set

    fun addNotesInRoute(stateNotes: MutableState<Notes?>) {
        currentNotes.apply {
            text = formState.notesText.data
            photos = photosList.toMutableList()
        }
        stateNotes.value = currentNotes
        clearField()
    }

    fun clearField() {
        currentNotes = currentNotes.copy(
            text = null,
            photos = mutableListOf()
        )
        photosList = mutableStateListOf(EMPTY_IMAGE_URI)
    }

    fun setData(notes: Notes?) {
        notes?.let {
            currentNotes = it
        }
    }

    fun createEventNotes(event: NotesEvent) {
        onNotesEvent(event)
    }

    private fun onNotesEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.EnteredNotesText -> {
                setNotesText(TextFieldValue(event.data ?: ""))
            }
            is NotesEvent.AddingPhoto -> {
                addPhoto(event.data)
            }
            is NotesEvent.RemovePhoto -> {
                removePhoto(event.data)
            }
        }
    }

    private fun setNotesText(newValue: TextFieldValue) {
        formState = formState.copy(
            notesText = formState.notesText.copy(
                data = newValue.text
            )
        )
    }

    private fun addPhoto(newPhoto: Uri) {
        viewModelScope.launch {
            photosList.add(0, newPhoto)
        }
    }

    private fun removePhoto(photo: Uri) {
        photosList.remove(photo)
    }

    private fun setPhotos(newValue: MutableList<Uri>) {
        formState.photos.clear()
        newValue.forEach { photo ->
            photosList.add(photo)
//            formState.photos
//                .add(
//                    NotesFieldPhoto(
//                        type = NotesDataType.PHOTOS,
//                        data = photo
//                    )
//                )
        }
    }
}