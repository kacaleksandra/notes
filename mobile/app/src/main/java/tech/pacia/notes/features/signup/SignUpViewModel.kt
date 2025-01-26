package tech.pacia.notes.features.signup

import android.util.Log
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

data class SignUpState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private var _uiState: MutableStateFlow<SignUpState> = MutableStateFlow(SignUpState())
    val uiState: StateFlow<SignUpState> = _uiState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpState(isLoading = true)

            if (email.isBlank() || password.isBlank()) {
                _uiState.value = SignUpState(error = "Email and password fields must not be empty")
                return@launch
            }

            when (val result = authRepository.signUp(email, password)) {
                is Exception -> {
                    Log.d(this::class.simpleName, "Failed to sign in: ${result.e}")
                    _uiState.value = SignUpState(error = "Fatal error while signing in")
                }

                is Error -> _uiState.value = SignUpState(error = result.message ?: "Sign in failed")
                is Success -> _uiState.value = SignUpState()
            }
        }
    }

    fun toggleLoading() {
        _uiState.value = SignUpState(isLoading = !_uiState.value.isLoading)
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
