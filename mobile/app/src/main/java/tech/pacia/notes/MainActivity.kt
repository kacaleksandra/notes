package tech.pacia.notes

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import tech.pacia.notes.data.AuthRepository
import tech.pacia.notes.data.NotesApiClient
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.data.TokenStore
import tech.pacia.notes.ui.theme.NotesTheme

private const val PREFERENCES_NAME = "preferences"

private val Context.dataStore by preferencesDataStore(
    name = PREFERENCES_NAME,
)

lateinit var globalDataStore: DataStore<Preferences>
lateinit var globalAuthRepository: AuthRepository
lateinit var globalNotesRepository: NotesRepository
lateinit var globalTokenStore: TokenStore
lateinit var globalApiClient: NotesApiClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        globalDataStore = dataStore

        globalTokenStore = TokenStore(dataStore = globalDataStore)

        globalApiClient = NotesApiClient(
            url = "http://10.0.2.2:3000/api/",
            tokenStore = globalTokenStore,
        )

        globalAuthRepository = AuthRepository(
            apiClient = globalApiClient.webservice,
            tokenStore = globalTokenStore,
        )

        globalNotesRepository = NotesRepository()

        setContent {
            NotesTheme {
                NotesNavHost()
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesTheme {
        Greeting("Android")
    }
}
