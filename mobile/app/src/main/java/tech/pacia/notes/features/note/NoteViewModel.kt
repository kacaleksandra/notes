package tech.pacia.notes.features.note

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.data.Success
import tech.pacia.notes.features.home.DisplayNote
import tech.pacia.notes.globalNotesRepository

sealed interface NoteState {
    data object Loading : NoteState

    data class Error(val message: String) : NoteState

    data class Success(
        val title: String,
        val content: String,
        val createdAt: Instant?,
        val categories: Set<String>,
        val isEdited: Boolean,
    ) : NoteState
}

// TODO: Refactor NotesState to simple data class with properties like isRefreshing and isError.
// No sealed classes are necessary since we'll simply listen to a flow from Room.

class NoteViewModel(
    private val noteId: Int?,
    private val notesRepository: NotesRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<NoteState> = MutableStateFlow(NoteState.Loading)
    val uiState: StateFlow<NoteState> = _uiState

    init {
        initialize()
    }

    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    fun initialize() = viewModelScope.launch {
        val noteId = this@NoteViewModel.noteId
        if (noteId == null) {
            _uiState.value = NoteState.Success(
                title = "",
                content = "",
                categories = setOf(),
                createdAt = null,
                isEdited = false,
            )
            return@launch
        }

        _uiState.value = NoteState.Loading

        val note = when (val response = notesRepository.readNote(noteId)) {
            is Success ->
                DisplayNote(
                    content = response.data.content,
                    createdAt = response.data.createdAt,
                    id = response.data.id,
                    title = response.data.title,
                    categories = listOf(),
                )

            is tech.pacia.notes.data.Error -> {
                Log.i(
                    this::class.simpleName,
                    "Failed to load note with id $noteId: ${response.code} ${response.message}",
                )
                _uiState.value = NoteState.Error(
                    message = "Failed to load note with id $noteId - ${response.message} (${response.code}",
                )
                return@launch
            }

            is tech.pacia.notes.data.Exception -> {
                Log.w(
                    this::class.simpleName,
                    "Unexpected exception while loading note with id $noteId: ${response.e}",
                )
                _uiState.value = NoteState.Error(
                    message = "Failed to load note with id $noteId (unexpected exception)",
                )
                return@launch
            }
        }

        _uiState.value = NoteState.Success(
            title = note.title,
            content = note.content,
            categories = setOf(),
            createdAt = note.createdAt,
            // createdAt = null, //
            isEdited = false,
        )
    }

    fun onTitleEdited(newTitle: String) {
        val state = _uiState.value
        if (state !is NoteState.Success) return

        _uiState.value = state.copy(title = newTitle, isEdited = true)
    }

    fun onContentEdited(newContent: String) {
        val state = _uiState.value
        if (state !is NoteState.Success) return

        _uiState.value = state.copy(content = newContent, isEdited = true)
    }

    fun saveNote() = viewModelScope.launch {
        val state = _uiState.value
        if (state !is NoteState.Success) return@launch

        if (noteId != null) {
            notesRepository.updateNote(
                id = noteId,
                title = state.title,
                content = state.content,
                categoryIds = listOf(),
            )
        } else {
            notesRepository.createNote(
                title = state.title,
                content = state.content,
                categoryIds = listOf(),
            )
        }

        _uiState.value = state.copy(isEdited = false)
    }

    companion object {
        val NOTE_ID_KEY = object : CreationExtras.Key<String> {}

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val noteId = extras[NOTE_ID_KEY]?.toIntOrNull()
                return NoteViewModel(noteId = noteId, notesRepository = globalNotesRepository) as T
            }
        }
    }
}
