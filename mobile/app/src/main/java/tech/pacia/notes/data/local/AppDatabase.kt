package tech.pacia.notes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(entities = [Note::class], version = 1)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}

class Converters {
    @TypeConverter
    fun fromSet(value: Set<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toSet(value: String) = Json.decodeFromString<Set<String>>(value)
}
