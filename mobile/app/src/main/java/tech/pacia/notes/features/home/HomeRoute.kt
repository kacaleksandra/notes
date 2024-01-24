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
    val homeViewModel = viewModel(
        modelClass = HomeViewModel::class.java,
        factory = HomeViewModel.Factory,
    )

    val notesUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        notesUiState = notesUiState,
        onCategoryClick = homeViewModel::toggleCategorySelected,
        onNavigateToNote = onNavigateToNote,
        onDeleteNote = homeViewModel::deleteNote,
        onRefresh = homeViewModel::refresh,
        onSignOut = onSignOut,
    )
}
