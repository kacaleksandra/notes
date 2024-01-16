package tech.pacia.notes.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.pacia.notes.data.AuthRepository
import tech.pacia.notes.data.Error
import tech.pacia.notes.data.Exception
import tech.pacia.notes.data.Success
import tech.pacia.notes.globalAuthRepository

sealed interface SignInState {
    data object Neutral : SignInState
    data object Loading : SignInState
    data class Error(val message: String) : SignInState
    data object Success : SignInState
}

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<SignInState> = MutableStateFlow(SignInState.Neutral)
    val uiState: StateFlow<SignInState> = _uiState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInState.Loading

            if (email.isBlank() || password.isBlank()) {
                _uiState.value = SignInState.Error("Email and password fields must not be empty")
                return@launch
            }

            val result = authRepository.signIn(email, password)
            when (result) {
                is Exception -> _uiState.value = SignInState.Error("Fatal error while signing in")
                is Error -> _uiState.value = SignInState.Error(result.message ?: "Sign in failed")
                is Success -> _uiState.value = SignInState.Success
            }
        }
    }

    fun dismissError() {
        _uiState.value = SignInState.Neutral
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = SignInViewModel(authRepository = globalAuthRepository) as T
        }
    }
}
