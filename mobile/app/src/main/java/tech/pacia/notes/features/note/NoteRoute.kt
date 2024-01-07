package tech.pacia.notes.features.note

import androidx.compose.runtime.Composable

@Composable
fun NoteRoute(
    id: String,
    onNavigateUp: () -> Unit,
) {
    // TODO: fetch note details

    NoteScreen(
        title = "My first note",
        onNavigateUp = onNavigateUp
    )
}
