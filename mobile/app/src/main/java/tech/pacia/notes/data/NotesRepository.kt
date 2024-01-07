package tech.pacia.notes.data

data class Note(
    val id: String,
    val title: String,
    val content: String,
)

class NotesRepository {
    suspend fun loadNotes(): List<Note> {
        return notes
    }

    suspend fun deleteNoteById(noteId: String) {
        for (note in notes) {
            if (note.id == noteId) {
                notes.remove(note)
                break
            }
        }
    }
}


val notes = mutableListOf(
    Note(
        id = "1",
        title = "Hey there!",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "2",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "3",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "4",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "5",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "6",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "7",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "8",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
    Note(
        id = "9",
        title = "This is a very long title that will be truncated",
        content = "This is a note. You can edit it by clicking on it.",
    ),
)
