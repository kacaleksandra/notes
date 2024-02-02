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

    suspend fun readNote(noteId: Int): NetworkResult<Note> {
        return callSafely { apiClient.readNote(id = noteId) }
    }

    suspend fun updateNote(id: Int, title: String, content: String, categoryIds: List<String>) {
        callSafely {
            apiClient.updateNote(
                id = id,
                UpsertNoteRequest(
                    title = title,
                    content = content,
                    categoryIds = categoryIds,
                ),
            )
        }
    }

    suspend fun deleteNoteById(noteId: Int) {
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

    suspend fun deleteCategory(id: Int) {
        callSafely { apiClient.deleteCategory(id = id) }
    }
}
