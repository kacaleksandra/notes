package tech.pacia.notes.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeRoute(
    onNavigateToNote: (noteId: String) -> Unit,
    onSignOut: () -> Unit,
) {
    val notesViewModel = viewModel(modelClass = NotesViewModel::class.java)
    val notesUiState by notesViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        notesUiState = notesUiState,
        onCategoryClick = { /*TODO*/ },
        onNavigateToNote = onNavigateToNote,
        onDeleteNote = notesViewModel::deleteNote,
        onSignOut = onSignOut,
    )
}
