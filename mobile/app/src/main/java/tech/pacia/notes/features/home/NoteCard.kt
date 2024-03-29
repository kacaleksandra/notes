package tech.pacia.notes.features.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: DisplayNote,
    onClick: () -> Unit = {},
    onSelect: () -> Unit = {},
    onCategoryClick: ((categoryId: Int) -> Unit)? = {},
    selected: Boolean = false,
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "border animation",
    )

    Card(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onSelect,
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 1.dp, color = borderColor),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (category in note.categories) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        FilterChip(
                            selected = true,
                            onClick = { onCategoryClick?.invoke(category.id) },
                            label = { Text(category.title) },
                        )
                    }
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun NoteCardPreview() {
    NotesTheme {
        NoteCard(note = NotesRepository.notes.first())
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCardSelectedPreview() {
    NotesTheme {
        NoteCard(note = NotesRepository.notes.first(), selected = true)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NoteCardPreviewDark() {
    NotesTheme {
        NoteCard(note = NotesRepository.notes.last())
    }
}
*/
