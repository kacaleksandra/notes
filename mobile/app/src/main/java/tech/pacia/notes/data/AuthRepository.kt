package tech.pacia.notes.data

import kotlinx.coroutines.flow.Flow

data class User(
    val email: String,
    val accessToken: String,
)

class AuthRepository(
    private val apiClient: NotesApi,
    private val tokenStore: TokenStore,
) {
    fun accessToken(): Flow<String?> = tokenStore.accessTokenUpdates()

    suspend fun signUp(email: String, password: String): NetworkResult<Unit> {
        return callSafely {
            apiClient.signUp(UserRequest(email = email, password = password))
        }
    }

    suspend fun signIn(email: String, password: String): NetworkResult<SignInResponse> {
        val result = callSafely {
            apiClient.signIn(UserRequest(email = email, password = password))
        }

        when (result) {
            is Success -> tokenStore.persistToken(result.data.accessToken)
            else -> Unit
        }

        return result
    }

    suspend fun signOut() = tokenStore.clearToken()
}
