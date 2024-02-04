package tech.pacia.notes.features.note

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import tech.pacia.notes.ui.theme.NotesTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    title: String = "",
    content: String = "",
    createdAt: String? = null,
    isEdited: Boolean = false,
    isNewNote: Boolean = false,
    onNavigateUp: () -> Unit = {},
    onTitleEdited: (String) -> Unit = {},
    onContentEdited: (String) -> Unit = { },
    onNoteSaved: () -> Unit = {},
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = 21,
        initialMinute = 37,
    )

    val selectedInstant: Instant? = if (datePickerState.selectedDateMillis == null) {
        null
    } else {
        val selectedTime = timePickerState.hour.hours + timePickerState.minute.minutes
        Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!) + selectedTime
    }

    selectedInstant?.toString()

    var showExitDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        modifier = modifier,
        sheetPeekHeight = 110.dp,
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        placeholder = { Text("Enter title") },
                        onValueChange = onTitleEdited,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onNoteSaved() }),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isEdited) {
                            onNavigateUp()
                        } else {
                            showExitDialog = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = content,
                    placeholder = { Text("Enter content") },
                    onValueChange = onContentEdited,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onNoteSaved() }),
                )

                if (isNewNote) {
                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp),
                        onClick = { onNoteSaved() },
                    ) {
                        Text("Save new note")
                    }
                }
            }
        },
        sheetContent = {
            Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar icon")
                Text(
                    if (selectedInstant == null) {
                        "No reminder set"
                    } else {
                        "Reminder set at ${formatDate(selectedInstant)}"
                    },
                )

                Spacer(modifier = Modifier.weight(1f))

                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    TextButton(onClick = { showDatePickerDialog = true }) {
                        Text(if (selectedInstant == null) "Add reminder" else "Change reminder")
                    }
                }
            }

            Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                Icon(Icons.Rounded.Menu, contentDescription = "Menu icon")
                Text(text = "No categories set")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = {
                }) {
                    Text("Set categories")
                }
            }

            Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                Icon(Icons.Rounded.Info, contentDescription = "Info icon")
                Text(text = "Created at")
                Spacer(modifier = Modifier.weight(1f))
                if (createdAt != null) {
                    Text(formatDate(Instant.parse(createdAt)))
                }
            }
        },
    )

    if (showDatePickerDialog) {
        AlertDialog(
            onDismissRequest = { showDatePickerDialog = false },
        ) {
            Surface(
                modifier = Modifier
                    .requiredWidth(360.0.dp)
                    .heightIn(max = 568.0.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    DatePicker(state = datePickerState)

                    TextButton(onClick = {
                        showDatePickerDialog = false
                        showTimePickerDialog = true
                    }) {
                        Text("Select time")
                    }
                }
            }
        }
    }

    if (showTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
        ) {
            Surface(
                modifier = Modifier
                    .requiredWidth(360.0.dp)
                    .heightIn(max = 568.0.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    TimePicker(state = timePickerState)

                    TextButton(onClick = {
                        showTimePickerDialog = true
                    }) {
                        Text("Select time")
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            icon = { Icon(Icons.Rounded.Warning, contentDescription = "Warning icon") },
            title = { Text(text = "You have unsaved changes") },
            text = { Text(text = "Are you sure you want to exit without saving changes?") },
            onDismissRequest = { showExitDialog = false },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onNavigateUp()
                }) {
                    Text("Exit without saving")
                }
            },
        )
    }
}

private fun formatDate(instant: Instant): String {
    val dateSt = instant.toString()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val formattedDate = LocalDateTime.parse(dateSt, dateFormatter)
    return DateTimeFormatter.ofPattern("MMMM dd, yyyy | hh:mma")
        .format(formattedDate) // August 04, 2017 | 6:39pm
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
