package com.example.traindriver.ui.screen.adding_screen.adding_notes

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.traindriver.domain.entity.Notes
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

    fun addNotesInRoute(stateNotes: MutableState<Notes?>) {
        val photosList = mutableListOf<String?>()
        formState.photos.forEach {
            photosList.add(it.data)
        }

        currentNotes.apply {
            text = formState.notesText.data
            photos = photosList
        }
        stateNotes.value = currentNotes
    }

    fun clearField() {
        currentNotes = currentNotes.copy(
            text = null,
            photos = mutableListOf()
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

    private fun setPhotos(newValue: MutableList<String?>) {
        formState.photos.clear()
        newValue.forEach { fieldText ->
            formState.photos
                .add(
                    NotesFieldText(
                        type = NotesDataType.NOTES_TEXT,
                        data = fieldText
                    )
                )
        }
    }
}