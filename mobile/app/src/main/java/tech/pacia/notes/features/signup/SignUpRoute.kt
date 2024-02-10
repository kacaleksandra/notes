package tech.pacia.notes.features.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignUpRoute(onNavigateBack: () -> Unit = {}) {
    val signUpViewModel: SignUpViewModel = viewModel(
        modelClass = SignUpViewModel::class.java,
        factory = SignUpViewModel.Factory,
    )

    val uiState by signUpViewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreen(
        uiState = uiState,
        onSignUpSubmitted = { email, password -> signUpViewModel.signUp(email, password) },
        onNavigateBack = onNavigateBack,
        toggle = signUpViewModel::toggleLoading,
    )
}
