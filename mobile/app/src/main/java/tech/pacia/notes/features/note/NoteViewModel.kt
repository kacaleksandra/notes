package tech.pacia.notes.features.note

import androidx.core.os.bundleOf
import androidx.datastore.preferences.core.doublePreferencesKey
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
import tech.pacia.notes.features.home.NotesState
import tech.pacia.notes.globalNotesRepository

sealed interface NoteState {
    data object Loading : NoteState

    data class Error(val message: String) : NoteState

    data class Success(
        val notes: List<Note>,
        val categories: Set<String>,
        val selectedCategoryIds: Set<String>,
        val selectedNotesIds: Set<String>,
    ) : NoteState {

        val selectedNotes: List<Note>
            get() {
                if (selectedCategoryIds.isEmpty()) return notes

                return notes.filter { note ->
                    note.categories.any { selectedCategoryIds.contains(it) }
                }
            }

        val selectionModeEnabled: Boolean
            get() = selectedNotesIds.isNotEmpty()
    }
}

// TODO: Refactor NotesState to simple data class with properties like isRefreshing and isError.
// No sealed classes are necessary since we'll simply listen to a flow from Room.

class NoteViewModel(
    private val noteId: String?,
    private val notesRepository: NotesRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<NoteState> = MutableStateFlow(NoteState.Loading)
    val uiState: StateFlow<NoteState> = _uiState

    init {
        refresh()
    }

    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    fun refresh() {
        viewModelScope.launch {
            val state = uiState.value
            val selectedCategories =
                if (state is NoteState.Success) state.selectedCategoryIds else setOf()

            _uiState.value = NoteState.Loading
            delay(1_000)

            try {
                _uiState.value = NoteState.Success(
                    categories = notesRepository.loadCategories(),
                    notes = notesRepository.loadNotes(),
                    selectedCategoryIds = selectedCategories,
                    selectedNotesIds = setOf(),
                )
            } catch (exception: Exception) {
                _uiState.value = NoteState.Error("Failed")
            }
        }
    }

    companion object {
        val NOTE_ID_KEY = object : CreationExtras.Key<String> {}

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val noteId = extras[NOTE_ID_KEY]
                return NoteViewModel(noteId = noteId, notesRepository = globalNotesRepository) as T
            }
        }
    }
}
