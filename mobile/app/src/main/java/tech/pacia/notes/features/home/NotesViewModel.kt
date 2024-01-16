package tech.pacia.notes.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.pacia.notes.data.Note
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.globalNotesRepository

sealed interface NotesState {
    data object Loading : NotesState

    data class Error(val message: String) : NotesState

    data class Success(
        val notes: List<Note>,
        val categories: Set<String>,
        val selectedCategories: Set<String>,
    ) : NotesState {

        val selectedNotes: List<Note>
            get() {
                if (selectedCategories.isEmpty()) return notes

                return notes.filter { note ->
                    note.categories.any { selectedCategories.contains(it) }
                }
            }
    }
}

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
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
            delay(1_000)

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

    fun toggleCategorySelected(categoryId: String) {
        val state = _uiState.value
        if (state !is NotesState.Success) return

        if (state.selectedCategories.contains(categoryId)) {
            _uiState.value = state.copy(
                selectedCategories = state.selectedCategories.minusElement(categoryId),
            )
        } else {
            _uiState.value = state.copy(
                selectedCategories = state.selectedCategories.plusElement(categoryId),
            )
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNoteById(noteId)
            refresh()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = NotesViewModel(notesRepository = globalNotesRepository) as T
        }
    }
}
