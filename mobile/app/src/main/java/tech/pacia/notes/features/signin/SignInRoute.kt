package tech.pacia.notes.features.signin

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.pacia.notes.ui.theme.NotesTheme

@Composable
fun SignInRoute(onNavigateToHome: () -> Unit = {}) {
    val signInViewModel: SignInViewModel = viewModel(
        modelClass = SignInViewModel::class.java,
        factory = SignInViewModel.Factory,
    )

    val signInState by signInViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(signInState) {
        if (signInState is SignInState.Success) {
            onNavigateToHome()
        }
    }

    SignInScreen(
        onDismissError = signInViewModel::dismissError,
        onSignInSubmitted = signInViewModel::signIn,
        signInState = signInState,
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
