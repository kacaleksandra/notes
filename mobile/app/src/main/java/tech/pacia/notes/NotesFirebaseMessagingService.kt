package tech.pacia.notes

import com.google.firebase.messaging.FirebaseMessagingService

class NotesFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
