package tech.pacia.notes.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.pacia.notes.ui.theme.NotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToNote: (noteId: String) -> Unit = {},
    notesViewModel: NotesViewModel = viewModel(),
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My notes") },
            )
        },
    ) { paddingValues ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(paddingValues),
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 4.dp,
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                items = notesViewModel.notes.value,
                key = { note -> note.id },
                itemContent = { note ->
                    NoteCard(
                        title = note.title,
                        content = note.content,
                        onClick = { onNavigateToNote(note.id) },
                        onLongClick = { notesViewModel.deleteNote(note.id) },
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NotesTheme {
        HomeScreen()
    }
}
