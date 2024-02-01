package tech.pacia.notes.data

class NotesRepository(private val apiClient: NotesApi) {

    /** Notes **/
    suspend fun createNote(title: String, content: String) {
        callSafely {
            apiClient.createNote(
                UpsertNoteRequest(
                    title = title,
                    content = content,
                    categoryIds = listOf(),
                ),
            )
        }
    }

    suspend fun readNotes(): NetworkResult<List<Note>> {
        return callSafely { apiClient.readNotes() }
    }

    suspend fun readNote(noteId: String): NetworkResult<Note> {
        return callSafely { apiClient.readNote(id = noteId) }
    }

    suspend fun updateNote(title: String, content: String, categoryIds: List<String>) {
        callSafely {
            apiClient.updateNote(
                UpsertNoteRequest(
                    title = title,
                    content = content,
                    categoryIds = categoryIds,
                ),
            )
        }
    }

    suspend fun deleteNoteById(noteId: String) {
        callSafely { apiClient.deleteNote(id = noteId) }
    }

    /** Categories **/

    suspend fun createCategory(title: String) {
        callSafely {
            apiClient.createCategory(CreateCategoryRequest(title = title))
        }
    }

    suspend fun readCategories(): NetworkResult<List<Category>> {
        return callSafely { apiClient.readCategories() }
    }

    suspend fun deleteCategory(id: String) {
        callSafely { apiClient.deleteCategory(id = id) }
    }
}
