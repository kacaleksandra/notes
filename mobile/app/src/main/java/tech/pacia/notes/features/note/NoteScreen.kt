package tech.pacia.notes.features.note

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import tech.pacia.notes.ui.theme.NotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    title: String,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(title) }) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "NoteScreen",
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO, showSystemUi = true)
@Composable
fun NoteScreenPreview() {
    NotesTheme {
        NoteScreen(
            title = "My first note",
            onNavigateUp = {},
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    NotesTheme {
        NoteScreen(
            title = "My first note",
            onNavigateUp = {},
        )
    }
}
