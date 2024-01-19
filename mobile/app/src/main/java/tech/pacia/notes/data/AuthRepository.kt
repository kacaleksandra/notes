package tech.pacia.notes.data

import retrofit2.HttpException
import retrofit2.Response

data class User(
    val email: String,
    val accessToken: String,
)

class AuthRepository(
    private val apiClient: NotesApi,
    private val tokenStore: TokenStore,
) {

    /*fun accessToken(): String? {
        return runBlocking { dataStore.data.first()[PreferencesKeys.USER_ACCESS_TOKEN] }
    }


    val userFlow: Flow<User?> = dataStore.data.map { preferences ->
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
            else -> Unit
        }

        return result
    }

    suspend fun signOut() = tokenStore.clearToken()

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T : Any> callSafely(apiMethod: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiMethod()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Success(body)
            } else {
                Error(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            Error(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            Exception(e)
        }
    }
}
