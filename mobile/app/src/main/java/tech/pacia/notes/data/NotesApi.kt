package tech.pacia.notes.data

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/** REQUESTS **/

@Serializable
data class UserRequest(
    val email: String,
    val password: String,
)

@Serializable
data class SignInResponse(
    val accessToken: String,
)

@Serializable
data class UpsertNoteRequest(
    val title: String,
    val content: String,
    val categoryIds: List<String>,
)

@Serializable
data class CreateCategoryRequest(
    val title: String,
)

@Serializable
data class CreateNotificationRequest(
    val date: Instant,
    val noteId: Int,
)

/** RESPONSES **/

@Serializable
data class Note(
    val content: String,
    // kotlin.serialization automatically supports kotlinx.datetime
    @SerialName("created_at") val createdAt: Instant,
    val id: Int,
    val title: String,
    @SerialName("categories") val categoryIds: List<Int>,
)

@Serializable
data class Category(
    val id: Int,
    val title: String,
)

interface NotesApi {
    @POST("users")
    suspend fun signUp(@Body user: UserRequest): Response<Unit>

    @POST("auth/login")
    suspend fun signIn(@Body user: UserRequest): Response<SignInResponse>

    /** NOTES **/

    @POST("notes")
    suspend fun createNote(@Body note: UpsertNoteRequest): Response<Unit>

    @GET("notes")
    suspend fun readNotes(): Response<List<Note>>

    @GET("notes/{id}")
    suspend fun readNote(@Path("id") id: Int): Response<Note>

    @PATCH("notes/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body note: UpsertNoteRequest): Response<Unit>

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Int): Response<Unit>

    /** CATEGORIES **/

    @POST("categories")
    suspend fun createCategory(@Body note: CreateCategoryRequest): Response<Unit>

    @GET("categories")
    suspend fun readCategories(): Response<List<Category>>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<Unit>

    /** NOTIFICATIONS **/

    @POST("notifications")
    suspend fun createNotification(@Body notification: CreateNotificationRequest): Response<Unit>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Int): Response<Unit>
}

class NotesApiClient(
    private val url: String,
    private val tokenStore: TokenStore,
) {
    private val builder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor { chain ->
        val accessToken = tokenStore.accessToken()
        Log.d("NotesApiClient", "access token: $accessToken")

        if (accessToken == null) {
            return@addInterceptor chain.proceed(chain.request())
        }

        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return@addInterceptor chain.proceed(request)
    }

    private val httpClient: OkHttpClient = builder.build()

    private val json: Json = Json {
        ignoreUnknownKeys = true
    }

    val webservice: NotesApi = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(httpClient)
        .build()
        .create(NotesApi::class.java)
}
