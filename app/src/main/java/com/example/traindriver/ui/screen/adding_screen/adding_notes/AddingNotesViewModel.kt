package com.example.traindriver.ui.screen.adding_screen.adding_notes

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.traindriver.domain.entity.Notes
import com.example.traindriver.ui.screen.photo.EMPTY_IMAGE_URI
import kotlin.properties.Delegates

class AddingNotesViewModel : ViewModel() {
    private var currentNotes: Notes by Delegates.observable(
        initialValue = Notes()
    ) { _, _, notes ->
        setNotesText(TextFieldValue(notes.text ?: ""))
        setPhotos(notes.photos)
    }

    var formState by mutableStateOf(
        NotesFormState(
            id = currentNotes.id,
            photos = mutableStateListOf(NotesFieldPhoto(EMPTY_IMAGE_URI))
        )
    )
        private set

    fun addNotesInRoute(stateNotes: MutableState<Notes?>) {
        val photoList = mutableListOf<Uri>()
        formState.photos.forEach {
            if (it.data != EMPTY_IMAGE_URI) {
                photoList.add(it.data)
            }
        }
        currentNotes.apply {
            text = formState.notesText.data
            photos = photoList
        }
        stateNotes.value = currentNotes
        clearField()
    }

    fun clearField() {
        formState = formState.copy(
            notesText = NotesFieldText(data = null),
            photos = mutableStateListOf(NotesFieldPhoto(EMPTY_IMAGE_URI))
        )
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
        formState.photos.add(1, NotesFieldPhoto(newPhoto))
    }

    private fun removePhoto(photo: Uri) {
        val element = formState.photos.find {
            it.data == photo
        }
        formState.photos.remove(element)
    }

    private fun setPhotos(newValue: MutableList<Uri>) {
        formState.photos = mutableStateListOf(NotesFieldPhoto(data = EMPTY_IMAGE_URI))
        newValue.forEach { photo ->
            formState.photos.add(NotesFieldPhoto(photo))
        }
    }
}