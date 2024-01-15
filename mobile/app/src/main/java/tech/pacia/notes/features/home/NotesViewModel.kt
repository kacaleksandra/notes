package tech.pacia.notes.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.pacia.notes.data.Note
import tech.pacia.notes.data.NotesRepository

sealed interface NotesState {
    data object Loading : NotesState

    data class Error(val message: String) : NotesState

    data class Success(
        val notes: List<Note>,
        val categories: Set<String>,
        val selectedCategories: Set<String>,
    ) : NotesState
}

class NotesViewModel(private val notesRepository: NotesRepository = NotesRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<NotesState> = MutableStateFlow(NotesState.Loading)
    val uiState: StateFlow<NotesState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val state = uiState.value
            val selectedCategories =
                if (state is NotesState.Success) state.selectedCategories else setOf()

            _uiState.value = NotesState.Loading

            try {
                _uiState.value = NotesState.Success(
                    categories = notesRepository.loadCategories(),
                    notes = notesRepository.loadNotes(),
                    selectedCategories = selectedCategories,
                )
            } catch (exception: Exception) {
                _uiState.value = NotesState.Error("Failed")
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNoteById(noteId)
            refresh()
        }
    }
}
