package tech.pacia.notes.features.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
            // TODO: Ugly hack to work around the lack of proper DI
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
    categories: Set<String> = setOf(),
    selectedCategories: Set<String> = setOf(),
    notes: List<Note> = listOf(),
    onCategoryClick: (category: String) -> Unit = {},
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
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (category in categories) {
                    FilterChip(
                        selected = selectedCategories.contains(category),
                        onClick = { onCategoryClick(category) },
                        label = { Text(category) },
                    )
                }
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 8.dp,
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = notes,
                    key = { note -> note.id },
                    itemContent = { note ->
                        NoteCard(
                            note = note,
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
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    NotesTheme {
        HomeScreen(
            notes = NotesRepository.notes,
            categories = setOf("All") + NotesRepository.categories,
            selectedCategories = setOf("All"),
        )
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    NotesTheme {
        HomeScreen(
            notes = NotesRepository.notes,
            categories = setOf("All") + NotesRepository.categories,
            selectedCategories = setOf("Shopping"),
        )
    }
}
