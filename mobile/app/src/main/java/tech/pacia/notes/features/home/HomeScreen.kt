package tech.pacia.notes.features.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import tech.pacia.notes.Greeting
import tech.pacia.notes.ui.theme.NotesTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToNote: (noteId: String) -> Unit,
) {
    Scaffold { paddingValues ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NotesTheme {
        HomeScreen(
            onNavigateToNote = { }
        )
    }
}
