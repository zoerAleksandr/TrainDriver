package com.example.traindriver.ui.screen.adding_screen.adding_notes

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

enum class NotesDataType {
    NOTES_TEXT, PHOTOS
}

data class NotesFieldText(
    val data: String? = null,
    val type: NotesDataType
)

data class NotesFormState(
    val id: String,
    val notesText: NotesFieldText = NotesFieldText(type = NotesDataType.NOTES_TEXT),
    var photos: SnapshotStateList<NotesFieldText> =
        mutableStateListOf(NotesFieldText(type = NotesDataType.PHOTOS))
)

sealed class NotesEvent {
    data class EnteredNotesText(val data: String?) : NotesEvent()
    data class AddingPhoto(val data: String?) : NotesEvent()
}