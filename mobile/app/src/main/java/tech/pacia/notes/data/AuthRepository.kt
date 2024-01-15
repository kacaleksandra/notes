package tech.pacia.notes.data

import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import retrofit2.Response

data class User(
    val email: String,
)

object AuthRepository {
    private val apiClient: NotesApi = NotesApiClient.webservice

    // val user: StateFlow<User> listen for accessToken in datastore

    suspend fun signUp(email: String, password: String): NetworkResult<Unit> {
        return callSafely {
            apiClient.signUp(UserRequest(email = email, password = password))
        }
    }

    suspend fun signIn(email: String, password: String): NetworkResult<SignInResponse> {
        return callSafely {
            apiClient.signIn(UserRequest(email = email, password = password))
        }
    }

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
