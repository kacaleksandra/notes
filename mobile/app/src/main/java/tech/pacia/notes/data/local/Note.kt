package tech.pacia.notes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "categories") val categories: Set<String>,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime, // TODO: Respect timezones
)
