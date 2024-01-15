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
data class SignInResponse(val accessToken: String)

interface NotesApi {
    @POST("users")
    suspend fun signUp(@Body user: UserRequest): Response<Unit>

    @POST("auth/login")
    suspend fun signIn(@Body user: UserRequest): Response<SignInResponse>

    @POST("notes")
    suspend fun createNote(@Body note: Note): Response<Unit>
}

object NotesApiClient {
    private const val URL = "http://10.0.2.2:3000/api/"

    // Adding authorization token to every request
    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor { chain ->
            // TODO: Get token from local storage
            val accessToken = ""

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            Log.d("Token", accessToken)
            chain.proceed(request)
        }

    private val httpClient: OkHttpClient = builder.build()

    val webservice: NotesApi = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(httpClient)
        .build()
        .create(NotesApi::class.java)
}
