package tech.pacia.notes.features.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.pacia.notes.data.Note
import tech.pacia.notes.data.NotesRepository

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    private val _notes: MutableState<List<Note>> = mutableStateOf(value = emptyList())
    val notes: State<List<Note>>
        get() = _notes

    fun refresh() {
        viewModelScope.launch {
            _notes.value = notesRepository.loadNotes()
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNoteById(noteId)
        }
    }

    private suspend fun getNotes(): List<Note> {
        return notesRepository.loadNotes()
    }
}
