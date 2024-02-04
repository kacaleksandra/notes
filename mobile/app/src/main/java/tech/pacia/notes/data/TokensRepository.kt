package tech.pacia.notes.data

class TokensRepository(private val apiClient: NotesApi) {
    suspend fun createToken(token: String) {
        callSafely {
            apiClient.createToken(CreateTokenRequest(token = token))
        }
    }
}
