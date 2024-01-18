package tech.pacia.notes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes")
    fun getAll(): List<Note>

    @Query("SELECT * FROM notes WHERE id IN (:ids)")
    fun getAllByIds(ids: List<String>): List<Note>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
           "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Note

    @Insert
    fun insertAll(vararg users: Note)

    @Delete
    fun delete(user: Note)
}
