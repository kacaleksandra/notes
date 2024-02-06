package tech.pacia.notes.features.signin

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.pacia.notes.ui.theme.NotesTheme

@Composable
fun SignInRoute(
    onNavigateToHome: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
) {
    val signInViewModel: SignInViewModel = viewModel(
        modelClass = SignInViewModel::class.java,
        factory = SignInViewModel.Factory,
    )

    val signInState by signInViewModel.uiState.collectAsStateWithLifecycle()
    val token by signInViewModel.token.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(token) {
        if (token != null) {
            onNavigateToHome()
        }
    }

    SignInScreen(
        onDismissError = signInViewModel::dismissError,
        onSignInSubmitted = signInViewModel::signIn,
        signInState = signInState,
        onNavigateToSignUp = onNavigateToSignUp,
    )
}

@Composable
@Preview(showSystemUi = true)
private fun SignInRoutePreview() {
    NotesTheme {
        SignInRoute()
    }
}

@Composable
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
private fun SignInRoutePreviewDark() {
    NotesTheme {
        SignInRoute()
    }
}
