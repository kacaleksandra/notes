package tech.pacia.notes.features.signup

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignUpRoute(onNavigateBack: () -> Unit = {}) {
    val signUpViewModel: SignUpViewModel = viewModel(
        modelClass = SignUpViewModel::class.java,
        factory = SignUpViewModel.Factory,
    )

    SignUpScreen(
        onSignUpSubmitted = { email, password -> signUpViewModel.signUp(email, password) },
        onNavigateBack = onNavigateBack,
    )
}
