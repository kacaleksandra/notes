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
        val selectedCategoryIds: Set<String>,
        val selectedNotesIds: Set<String>,
    ) : NotesState {

        val selectedNotes: List<Note>
            get() {
                if (selectedCategoryIds.isEmpty()) return notes

                return notes.filter { note ->
                    note.categories.any { selectedCategoryIds.contains(it) }
                }
            }
    }
}

// TODO: Refactor NotesState to simple data class with properties like isRefreshing and isError.
// No sealed classes are necessary since we'll simply listen to a flow from Room.

class HomeViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<NotesState> = MutableStateFlow(NotesState.Loading)
    val uiState: StateFlow<NotesState> = _uiState

    init {
        refresh()
    }

    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    fun refresh() {
        viewModelScope.launch {
            val state = uiState.value
            val selectedCategories =
                if (state is NotesState.Success) state.selectedCategoryIds else setOf()

            _uiState.value = NotesState.Loading
            delay(1_000)

            try {
                _uiState.value = NotesState.Success(
                    categories = notesRepository.loadCategories(),
                    notes = notesRepository.loadNotes(),
                    selectedCategoryIds = selectedCategories,
                    selectedNotesIds = setOf(),
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

    fun selectNote(noteId: String) {
        val state = _uiState.value
        if (state !is NotesState.Success) return

        if (state.selectedNotesIds.contains(noteId)) {
            _uiState.value = state.copy(
                selectedNotesIds = state.selectedNotesIds.minusElement(noteId),
            )
        } else {
            _uiState.value = state.copy(
                selectedNotesIds = state.selectedNotesIds.plusElement(noteId),
            )
        }
    }

    fun selectCategory(categoryId: String) {
        val state = _uiState.value
        if (state !is NotesState.Success) return

        if (state.selectedCategoryIds.contains(categoryId)) {
            _uiState.value = state.copy(
                selectedCategoryIds = state.selectedCategoryIds.minusElement(categoryId),
            )
        } else {
            _uiState.value = state.copy(
                selectedCategoryIds = state.selectedCategoryIds.plusElement(categoryId),
            )
        }
    }

    fun deleteSelectedNotes() {
        val state = _uiState.value
        if (state !is NotesState.Success) return

        if (state.selectedNotesIds.isEmpty()) return

        _uiState.value = state.copy(
            notes = state.notes.filterNot { note -> state.selectedNotesIds.contains(note.id) }
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = HomeViewModel(notesRepository = globalNotesRepository) as T
        }
    }
}
