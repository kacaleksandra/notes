package tech.pacia.notes.features.note

import androidx.compose.runtime.Composable
import tech.pacia.notes.features.home.HomeScreen

@Composable
fun NoteRoute(
    id: String,
    onNavigateUp: () -> Unit,
) {
    // TODO: fetch note details

    NoteScreen(
        onNavigateUp = onNavigateUp
    )
}
