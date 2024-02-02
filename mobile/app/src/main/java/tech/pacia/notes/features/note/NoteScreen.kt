package tech.pacia.notes.features.note

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.pacia.notes.ui.theme.NotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    title: String = "",
    content: String = "",
    createdAt: String = "",
    isNewNote: Boolean = false,
    onNavigateUp: () -> Unit = {},
    onTitleEdited: (String) -> Unit = {},
    onContentEdited: (String) -> Unit = { },
    onNoteSaved: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        onValueChange = onTitleEdited,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onNoteSaved() }),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = content,
                onValueChange = onContentEdited,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onNoteSaved() }),
            )

            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                text = "Created $createdAt",
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
            title = "My first note with way too long text that overflows",
            onNavigateUp = {},
        )
    }
}
