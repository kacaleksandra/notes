package tech.pacia.notes.features.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignInRoute(
    onNavigateToHome: () -> Unit,
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
