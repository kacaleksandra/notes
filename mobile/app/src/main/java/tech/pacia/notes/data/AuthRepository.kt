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

//    fun accessToken(): String? {
//        return runBlocking { dataStore.data.first()[PreferencesKeys.USER_ACCESS_TOKEN] }
//    }

    fun accessToken(): Flow<String?> = tokenStore.accessTokenUpdates()

    /* val userFlow: Flow<User?> = dataStore.data.map { preferences ->
         val accessToken = preferences[PreferencesKeys.USER_ACCESS_TOKEN]
         val email = preferences[PreferencesKeys.USER_EMAIL]
         if (accessToken == null || email == null) {
             return@map null
         }

         return@map User(
             email = email, accessToken = accessToken
         )
     }*/

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
            /*dataStore.edit { preferences ->
                preferences[PreferencesKeys.USER_ACCESS_TOKEN] = result.data.accessToken
                preferences[PreferencesKeys.USER_EMAIL] = result.data.email
            }*/

            else -> Unit
        }

        return result
    }

    suspend fun signOut() = tokenStore.clearToken()
}
