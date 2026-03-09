package com.wishly.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishly.app.data.api.AuthApi
import com.wishly.app.data.repository.AuthRepository
import com.wishly.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val password: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorCode: Int? = null,
    val isSuccess: Boolean = false,
    val successMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            error = null,
            isSuccess = false
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            error = null,
            isSuccess = false
        )
    }

    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email is required")
            return
        }

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(error = "Invalid email format")
            return
        }

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Password is required")
            return
        }


        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Welcome back!")
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        errorCode = result.code
                    )
                }

                is Result.Loading -> {}
            }
        }
    }

    fun register() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email is required")
            return
        }

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(error = "Invalid email format")
            return
        }

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Password is required")
            return
        }

        if (!isValidPassword(password)) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.register(email, password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Account created successfully!")
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        errorCode = result.code
                    )
                }

                is Result.Loading -> {}
            }
        }
    }

    fun cleanError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false, successMessage = null)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

}