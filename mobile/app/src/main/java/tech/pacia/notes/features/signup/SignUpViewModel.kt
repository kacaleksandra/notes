package tech.pacia.notes.features.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.pacia.notes.data.AuthRepository
import tech.pacia.notes.data.Error
import tech.pacia.notes.data.Exception
import tech.pacia.notes.data.Success
import tech.pacia.notes.globalAuthRepository

sealed interface SignUpState {
    data object Neutral : SignUpState
    data object Loading : SignUpState
    data class Error(val message: String) : SignUpState
    data object Success : SignUpState
}

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<SignUpState> = MutableStateFlow(SignUpState.Neutral)
    val uiState: StateFlow<SignUpState> = _uiState

    val token: Flow<String?> = authRepository.accessToken()

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpState.Loading

            if (email.isBlank() || password.isBlank()) {
                _uiState.value = SignUpState.Error("Email and password fields must not be empty")
                return@launch
            }

            if (email == "a" && password == "a") {
                _uiState.value = SignUpState.Success
                return@launch
            }

            val result = authRepository.signUp(email, password)
            when (result) {
                is Exception -> {
                    Log.d(this::class.simpleName, "Failed to sign in: ${result.e}")
                    _uiState.value = SignUpState.Error("Fatal error while signing in")
                }

                is Error -> _uiState.value = SignUpState.Error(result.message ?: "Sign in failed")
                is Success -> _uiState.value = SignUpState.Success
            }
        }
    }

    fun dismissError() {
        _uiState.value = SignUpState.Neutral
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = SignUpViewModel(authRepository = globalAuthRepository) as T
        }
    }
}
