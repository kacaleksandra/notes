package tech.pacia.notes.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeRoute(
    onNavigateToNote: (noteId: Int?) -> Unit,
    onSignOut: () -> Unit,
) {
    val homeViewModel = viewModel(
        modelClass = HomeViewModel::class.java,
        factory = HomeViewModel.Factory,
    )

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToNote = onNavigateToNote,
        onDeleteSelectedNotes = homeViewModel::deleteSelectedNotes,
        onSelectNote = homeViewModel::selectNote,
        onSelectCategory = homeViewModel::selectCategory,
        onRefresh = homeViewModel::refresh,
        onSignOut = {
            homeViewModel.signOut()
            onSignOut()
        },
    )
}
