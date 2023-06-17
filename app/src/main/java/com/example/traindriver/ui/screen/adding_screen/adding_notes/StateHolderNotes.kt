package com.example.traindriver.ui.screen.adding_screen.adding_notes

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.traindriver.ui.screen.photo.EMPTY_IMAGE_URI

enum class NotesDataType {
    NOTES_TEXT, PHOTOS
}

data class NotesFieldText(
    val data: String? = null,
    val type: NotesDataType = NotesDataType.NOTES_TEXT
)

data class NotesFieldPhoto(
    val data: Uri = EMPTY_IMAGE_URI,
    val type: NotesDataType = NotesDataType.PHOTOS
)

data class NotesFormState(
    val id: String,
    val notesText: NotesFieldText = NotesFieldText(type = NotesDataType.NOTES_TEXT),
    var photos: SnapshotStateList<NotesFieldPhoto> =
        mutableStateListOf(NotesFieldPhoto())
)

sealed class NotesEvent {
    data class EnteredNotesText(val data: String?) : NotesEvent()
    data class AddingPhoto(val data: Uri) : NotesEvent()
    data class RemovePhoto(val data: Uri) : NotesEvent()
}