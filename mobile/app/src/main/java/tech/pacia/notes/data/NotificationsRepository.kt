package tech.pacia.notes.data

import kotlinx.datetime.Instant

class NotificationsRepository(private val apiClient: NotesApi) {
    suspend fun createNotification(noteId: Int, date: Instant) {
        callSafely {
            apiClient.createNotification(
                CreateNotificationRequest(
                    date = date,
                    noteId = noteId,
                ),
            )
        }
    }

    suspend fun deleteNotification(notificationId: Int) {
        callSafely { apiClient.deleteNotification(id = notificationId) }
    }

    suspend fun createToken(token: String) {
        callSafely {
            apiClient.createToken(CreateTokenRequest(token = token))
        }
    }
}
