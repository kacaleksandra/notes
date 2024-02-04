package tech.pacia.notes.features.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel

@Suppress("UnusedParameter")
@Composable
fun NoteRoute(
    id: Int?,
    onNavigateUp: () -> Unit,
) {
    val noteViewModel: NoteViewModel = viewModel(
        modelClass = NoteViewModel::class.java,
        factory = NoteViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(NoteViewModel.NOTE_ID_KEY, id.toString())
        },
    )

    val uiState by noteViewModel.uiState.collectAsStateWithLifecycle()

    NoteScreen(
        title = when (val state = uiState) {
            is NoteState.Loading -> "Loading..."
            is NoteState.Error -> "Failed to load note"
            is NoteState.Success -> state.title
        },
        content = when (val state = uiState) {
            is NoteState.Loading -> "Loading..."
            is NoteState.Error -> "Failed to load note"
            is NoteState.Success -> state.content
        },
        createdAt = when (val state = uiState) {
            is NoteState.Success -> state.createdAt
            else -> null
        },
        isEdited = when (val state = uiState) {
            is NoteState.Success -> state.isEdited
            else -> false
        },
        isNewNote = id == null,
        onTitleEdited = noteViewModel::onTitleEdited,
        onContentEdited = noteViewModel::onContentEdited,
        onNoteSaved = noteViewModel::saveNote,
        onNavigateUp = onNavigateUp,
    )
}
