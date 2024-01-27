package tech.pacia.notes.features.note

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel

@Suppress("UnusedParameter")
@Composable
fun NoteRoute(
    id: String,
    onNavigateUp: () -> Unit,
) {
    val homeViewModel = viewModel(
        modelClass = NoteViewModel::class.java,
        factory = NoteViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(NoteViewModel.NOTE_ID_KEY, id)
        },
    )

    val uiState = homeViewModel.uiState.collectAsStateWithLifecycle()

    NoteScreen(
        title = "My first note",
        onNavigateUp = onNavigateUp,
    )
}
