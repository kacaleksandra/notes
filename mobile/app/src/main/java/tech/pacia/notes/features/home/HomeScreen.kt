package tech.pacia.notes.features.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
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
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.pacia.notes.data.Note
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.ui.theme.NotesTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToNote: (noteId: String) -> Unit = {},
    notesViewModel: NotesViewModel = viewModel(
        factory = NotesViewModel.Factory,
        extras = MutableCreationExtras().apply {
            // FIXME: Ugly hack to work around the lack of proper DI
            set(NotesRepository.VM_KEY, NotesRepository())
        },
    ),
) {
    HomeScreen(
        modifier = modifier,
        notes = notesViewModel.notes.value,
        onNavigateToNote = onNavigateToNote,
        onDeleteNote = notesViewModel::deleteNote,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    notes: List<Note> = listOf(),
    onNavigateToNote: (noteId: String) -> Unit = {},
    onDeleteNote: (noteId: String) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My notes super app") },
            )
        },
    ) { paddingValues ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(paddingValues),
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = notes,
                key = { note -> note.id },
                itemContent = { note ->
                    NoteCard(
                        title = note.title,
                        content = note.content,
                        onClick = { onNavigateToNote(note.id) },
                        onLongClick = {
                            Log.d("XDDD lol", "Deleting note with id ${note.id}")
                            onDeleteNote(note.id)
                        },
                    )
                },
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    NotesTheme {
        HomeScreen(
            notes = NotesRepository.notes,
        )
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    NotesTheme {
        HomeScreen(
            notes = NotesRepository.notes,
        )
    }
}
