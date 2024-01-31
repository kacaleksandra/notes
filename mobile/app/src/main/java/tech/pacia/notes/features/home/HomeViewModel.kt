package tech.pacia.notes.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.pacia.notes.data.Category
import tech.pacia.notes.data.Note
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.data.Success
import tech.pacia.notes.globalNotesRepository

sealed interface NotesState {
    data object Loading : NotesState

    data class Error(val message: String) : NotesState

    data class Success(
        val notes: List<Note>,
        val categories: List<Category>,
        val selectedCategoryIds: Set<Int>,
        val selectedNotesIds: Set<Int>,
    ) : NotesState {

        val selectedNotes: List<Note>
            get() {
                if (selectedCategoryIds.isEmpty()) return notes

                return notes.filter { note ->
                    note.categoryIds.any { selectedCategoryIds.contains(it) }
                }
            }

        val selectionModeEnabled: Boolean
            get() = selectedNotesIds.isNotEmpty()
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
            // Save UI state before we start changing it
            val selectedCategories = when (val state = uiState.value) {
                is NotesState.Success -> state.selectedCategoryIds
                else -> setOf()
            }

            _uiState.value = NotesState.Loading

            val categories = when (val response = notesRepository.readCategories()) {
                is Success -> response.data
                is tech.pacia.notes.data.Error -> {
                    Log.i(
                        this::class.simpleName,
                        "Failed to load categories: ${response.code} ${response.message}",
                    )
                    _uiState.value = NotesState.Error(
                        message = "Failed to load categories - ${response.message} (${response.code}",
                    )
                    return@launch
                }

                is tech.pacia.notes.data.Exception -> {
                    Log.w(
                        this::class.simpleName,
                        "Unexpected exception while loading categories: ${response.e}",
                    )
                    _uiState.value = NotesState.Error(
                        message = "Failed to load categories (unexpected exception)",
                    )
                    return@launch
                }
            }

            val notes = when (val response = notesRepository.readNotes()) {
                is Success -> response.data
                is tech.pacia.notes.data.Error -> {
                    Log.i(
                        this::class.simpleName,
                        "Failed to load notes: ${response.code} ${response.message}",
                    )
                    _uiState.value = NotesState.Error(
                        message = "Failed to load notes - ${response.message} (${response.code}",
                    )
                    return@launch
                }

                is tech.pacia.notes.data.Exception -> {
                    Log.w(
                        this::class.simpleName,
                        "Unexpected exception while loading notes: ${response.e}",
                    )
                    _uiState.value = NotesState.Error(
                        message = "Failed to load notes (unexpected exception)",
                    )
                    return@launch
                }
            }

            try {
                _uiState.value = NotesState.Success(
                    categories = categories,
                    notes = notes,
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

    fun selectNote(noteId: Int) {
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

    fun selectCategory(categoryId: Int) {
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
            notes = state.notes.filterNot { note -> state.selectedNotesIds.contains(note.id) },
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
