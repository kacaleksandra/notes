package tech.pacia.notes.features.note

import androidx.compose.runtime.Composable

@Suppress("UnusedParameter")
@Composable
fun NoteRoute(
    id: String,
    onNavigateUp: () -> Unit,
) {
    NoteScreen(
        title = "My first note",
        onNavigateUp = onNavigateUp,
    )
}
