package tech.pacia.notes.features.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.pacia.notes.data.Note
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.globalNotesRepository

sealed interface NoteState {
    data object Loading : NoteState

    data class Error(val message: String) : NoteState

    data class Success(
        val title: List<Note>,
        val content: Set<String>,
        val categories: Set<String>,
    ) : NoteState
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
