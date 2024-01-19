package tech.pacia.notes.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes")
    suspend fun getNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE id IN (:ids)")
    suspend fun getNotesByIds(ids: List<String>): List<Note>

    @Upsert
    suspend fun upsertNote(note: Note)

    @Query("DELETE FROM notes WHERE id in (:ids)")
    suspend fun deleteNotes(ids: List<String>)
}
