package tech.pacia.notes.features.home

import androidx.compose.runtime.Composable
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

    val uiState = homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState.value,
        onNavigateToNote = onNavigateToNote,
        onDeleteSelectedNotes = homeViewModel::deleteSelectedNotes,
        onSelectNote = homeViewModel::selectNote,
        onSelectCategory = homeViewModel::selectCategory,
        onRefresh = homeViewModel::refresh,
        onSignOut = onSignOut,
    )
}
