package tech.pacia.notes

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import tech.pacia.notes.data.AuthRepository
import tech.pacia.notes.data.NotesApiClient
import tech.pacia.notes.data.NotesRepository
import tech.pacia.notes.data.NotificationsRepository
import tech.pacia.notes.data.TokenStore
import tech.pacia.notes.ui.theme.NotesTheme

private const val PREFERENCES_NAME = "preferences"

private val Context.dataStore by preferencesDataStore(
    name = PREFERENCES_NAME,
)

lateinit var globalDataStore: DataStore<Preferences>
lateinit var globalAuthRepository: AuthRepository
lateinit var globalNotesRepository: NotesRepository
lateinit var globalNotificationsRepository: NotificationsRepository
lateinit var globalTokenStore: TokenStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        globalDataStore = dataStore

        globalTokenStore = TokenStore(dataStore = globalDataStore)

        val apiClient = NotesApiClient(
            url = "http://10.0.2.2:3000/api/",
            tokenStore = globalTokenStore,
        ).webservice

        globalAuthRepository = AuthRepository(
            apiClient = apiClient,
            tokenStore = globalTokenStore,
        )

        globalNotesRepository = NotesRepository(
            apiClient = apiClient,
        )

        globalNotificationsRepository = NotificationsRepository(
            apiClient = apiClient,
        )

        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(this::class.simpleName, "fetch FCM token failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result

                lifecycleScope.launch {
                    globalNotificationsRepository.createToken(token)
                    Log.d(this::class.simpleName, "Sent FCM token to server")
                }

                Log.d(this::class.simpleName, "Got token: $token")
                Toast.makeText(baseContext, "Got token: $token", Toast.LENGTH_SHORT).show()
            },
        )

        setContent {
            NotesTheme {
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { granted ->
                        Toast.makeText(
                            baseContext,
                            "Notif perm granted: $granted",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                )

                LaunchedEffect(key1 = "test") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(POST_NOTIFICATIONS)
                    }
                }

                NotesNavHost()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // Inform user that that your app will not show notifications.
            Toast.makeText(baseContext, "No notification permission granted", Toast.LENGTH_SHORT)
                .show()

            askNotificationPermission()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result = ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
            if (result == PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
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
