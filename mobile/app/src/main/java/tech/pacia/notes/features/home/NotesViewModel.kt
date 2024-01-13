package tech.pacia.notes.features.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import tech.pacia.notes.data.Note
import tech.pacia.notes.data.NotesRepository

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _notes: MutableState<List<Note>> = mutableStateOf(value = emptyList())
    private val _categories: MutableState<Set<String>> = mutableStateOf(value = emptySet())
    private val _selectedCategories: MutableState<Set<String>> = mutableStateOf(value = emptySet())

    init {
        refresh()
    }

    val notes: State<List<Note>>
        get() = _notes

    val categories: State<Set<String>>
        get() = _categories

    val selectedCategories: State<Set<String>>
        get() = _selectedCategories

    fun setSelectedCategories(categories: Set<String>) {
        _selectedCategories.value = categories
    }

    fun refresh() {
        viewModelScope.launch {
            _categories.value = notesRepository.loadCategories()
            _notes.value = notesRepository.loadNotes()
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNoteById(noteId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val notesRepository = checkNotNull(extras[NotesRepository.VM_KEY])

                    return NotesViewModel(
                        notesRepository = notesRepository,
                    ) as T
                }
            }
    }
}
