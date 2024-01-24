package tech.pacia.notes.data

import kotlinx.datetime.LocalDateTime

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val categories: Set<String>,
    val createdAt: LocalDateTime, // TODO: Respect timezones
)

class NotesRepository(private val apiClient: NotesApi) {
    suspend fun loadCategories(): Set<String> {
        return categories
    }

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

    suspend fun createNote(note: Note) {
        apiClient.createNote(note)
    }

    companion object {

        val categories = mutableSetOf("Journals", "Shopping", "Ideas", "Stupid & Crazy", "Poems")

        val notes = mutableListOf(
            Note(
                id = "1",
                title = "Journal Entry 1",
                content = "Today was an amazing day!",
                categories = setOf("Journals"),
                createdAt = LocalDateTime.parse("2024-01-05T10:30"),
            ),
            Note(
                id = "2",
                title = "Shopping List",
                content = "Milk, Eggs, Bread, Vegetables",
                categories = setOf("Shopping"),
                createdAt = LocalDateTime.parse("2024-01-08T15:45"),
            ),
            Note(
                id = "3",
                title = "Idea for Project",
                content = "Create a new app that helps with productivity. Blah Blah Blah this was " +
                    "all generated by ChatGPT.",
                categories = setOf("Ideas"),
                createdAt = LocalDateTime.parse("2024-01-12T08:00"),
            ),
            Note(
                id = "4",
                title = "Journal Entry 2",
                content = "Spent some quality time with friends.",
                categories = setOf("Journals"),
                createdAt = LocalDateTime.parse("2024-01-15T18:20"),
            ),
            Note(
                id = "5",
                title = "Shopping for the Weekend",
                content = "Chicken, Pasta, Sauce, Snacks",
                categories = setOf("Shopping"),
                createdAt = LocalDateTime.parse("2024-01-20T12:00"),
            ),
            Note(
                id = "6",
                title = "Innovative Idea",
                content = "Develop a sustainable energy solution. No more coal mines, hell yeah.",
                categories = setOf("Ideas"),
                createdAt = LocalDateTime.parse("2024-01-25T09:30"),
            ),
            Note(
                id = "7",
                title = "What if...?",
                content = "If a cow had wings then it could fly. Whoaah, it'd be awesome! Realllly!",
                categories = setOf("Ideas", "Stupid & Crazy"),
                createdAt = LocalDateTime.parse("2024-01-25T21:37"),
            ),
        )
    }
}
