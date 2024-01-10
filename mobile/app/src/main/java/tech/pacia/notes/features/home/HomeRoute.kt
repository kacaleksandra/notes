package tech.pacia.notes.features.home

import androidx.compose.runtime.Composable

@Composable
fun HomeRoute(onNavigateToNote: (noteId: String) -> Unit) {
    HomeScreen(
        onNavigateToNote = onNavigateToNote,
    )
}
