package tech.pacia.notes.features.signin

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.pacia.notes.ui.theme.NotesTheme

@Composable
fun SignInRoute(
    onNavigateToHome: () -> Unit = {},
) {
    val signInViewModel: SignInViewModel = viewModel(modelClass = SignInViewModel::class.java)

    val inProgress = signInViewModel.inProgress.collectAsState().value
    val hasError = signInViewModel.hasError.collectAsState().value

    SignInScreen(
        inProgress = inProgress,
        hasError = hasError,
        onDismissError = { signInViewModel.dismissError() },
        onSignInSubmitted = { username, password ->
            signInViewModel.signIn(username, password, onNavigateToHome)
        },
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
