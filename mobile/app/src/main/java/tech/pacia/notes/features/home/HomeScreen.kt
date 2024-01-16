package tech.pacia.notes.features.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.ui.theme.NotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    notesUiState: NotesState,
    onCategoryClick: (category: String) -> Unit = {},
    onNavigateToNote: (noteId: String) -> Unit = {},
    onDeleteNote: (noteId: String) -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My notes super app") },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Sign out",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            when (notesUiState) {
                is NotesState.Loading -> {
                    CircularProgressIndicator()
                }

                is NotesState.Error -> {
                    Text(notesUiState.message)
                }

                is NotesState.Success -> {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Spacer(modifier = Modifier)
                        for (category in notesUiState.categories) {
                            FilterChip(
                                selected = notesUiState.selectedCategories.contains(category),
                                onClick = { onCategoryClick(category) },
                                label = { Text(category) },
                            )
                        }
                        Spacer(modifier = Modifier)
                    }

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = notesUiState.selectedNotes,
                            key = { note -> note.id },
                            itemContent = { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { onNavigateToNote(note.id) },
                                    onDelete = { onDeleteNote(note.id) },
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    NotesTheme {
        HomeScreen(
            notesUiState = NotesState.Success(
                notes = NotesRepository.notes,
                categories = setOf("All") + NotesRepository.categories,
                selectedCategories = setOf("All", "Shopping"),
            ),
        )
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    NotesTheme {
        HomeScreen(
            notesUiState = NotesState.Success(
                notes = NotesRepository.notes,
                categories = setOf("All") + NotesRepository.categories,
                selectedCategories = setOf("Shopping"),
            ),
        )
    }
}
