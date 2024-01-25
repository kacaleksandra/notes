package tech.pacia.notes.features.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.ui.theme.NotesTheme

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    notesUiState: NotesState,
    onNavigateToNote: (noteId: String) -> Unit = {},
    onDeleteSelectedNotes: () -> Unit = {},
    onSelectNote: (noteId: String) -> Unit = {},
    onSelectCategory: (category: String) -> Unit = {},
    onRefresh: () -> Unit = {},
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

                    if (notesUiState is NotesState.Success && notesUiState.selectedNotesIds.isNotEmpty()) {
                        IconButton(onClick = onDeleteSelectedNotes) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete selected notes",
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->

        val pullRefreshState = rememberPullRefreshState(
            refreshing = notesUiState is NotesState.Loading,
            onRefresh = onRefresh,
        )

        if (notesUiState is NotesState.Loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            return@Scaffold
        }

        if (notesUiState is NotesState.Error) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = notesUiState.message,
                )
            }

            return@Scaffold
        }

        if (notesUiState !is NotesState.Success) {
            return@Scaffold
        }

        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Spacer(modifier = Modifier)
                for (category in notesUiState.categories) {
                    FilterChip(
                        selected = notesUiState.selectedCategoryIds.contains(category),
                        onClick = { onSelectCategory(category) },
                        label = { Text(category) },
                    )
                }
                Spacer(modifier = Modifier)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState),
            ) {
                if (notesUiState.selectedNotes.isEmpty()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "You don't have any notes",
                    )
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
                                modifier = Modifier.animateItemPlacement(),
                                note = note,
                                onClick = {
                                    if (notesUiState.selectedNotesIds.isNotEmpty()) {
                                        onSelectNote(note.id)
                                    } else {
                                        onNavigateToNote(note.id)
                                    }
                                },
                                onSelect = { onSelectNote(note.id) },
                                selected = notesUiState.selectedNotesIds.contains(note.id),
                            )
                        },
                    )
                }

                PullRefreshIndicator(
                    refreshing = notesUiState is NotesState.Loading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    // backgroundColor = if (viewModel.state.value.isLoading) Color.Red else Color.Green,
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
            notesUiState = NotesState.Success(
                notes = NotesRepository.notes,
                categories = setOf("All") + NotesRepository.categories,
                selectedCategoryIds = setOf("All", "Shopping"),
                selectedNotesIds = setOf(),
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
                selectedCategoryIds = setOf(),
                selectedNotesIds = setOf(),
            ),
        )
    }
}
