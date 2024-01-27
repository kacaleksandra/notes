package tech.pacia.notes.data

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class UserRequest(
    val email: String,
    val password: String,
)

@Serializable
data class SignInResponse(
    val accessToken: String,
)

interface NotesApi {
    @POST("users")
    suspend fun signUp(@Body user: UserRequest): Response<Unit>

    @POST("auth/login")
    suspend fun signIn(@Body user: UserRequest): Response<SignInResponse>

    @POST("notes")
    suspend fun createNote(@Body note: Note): Response<Unit>
}

class NotesApiClient(
    private val url: String,
    private val tokenStore: TokenStore,
) {
    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val accessToken = tokenStore.accessToken()
            if (accessToken == null) {
                Log.d("NotesApiClient", "accessToken is null")
                chain.proceed(chain.request())
            }
            Log.d("NotesApiClient", "AccessToken: $accessToken")

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            chain.proceed(request)
        }

    private val httpClient: OkHttpClient = builder.build()

    val webservice: NotesApi = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(httpClient)
        .build()
        .create(NotesApi::class.java)
}
