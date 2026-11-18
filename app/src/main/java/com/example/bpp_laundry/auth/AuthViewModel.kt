package com.example.bpp_laundry.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bpp_laundry.data.model.UserData
import com.example.bpp_laundry.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: UserData?, val message: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val message: String, val userId: Int?) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginUiState.Error("Email dan password wajib diisi")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            val result = repository.login(email.trim(), password)
            result.onSuccess { response ->
                _loginState.value = LoginUiState.Success(
                    user = response.data,
                    message = response.message ?: "Login berhasil"
                )
            }.onFailure { error ->
                _loginState.value = LoginUiState.Error(
                    error.message ?: "Terjadi kesalahan saat autentikasi"
                )
            }
        }
    }

    fun register(fullName: String, email: String, phone: String?, password: String) {
        if (fullName.isBlank()) {
            _registerState.value = RegisterUiState.Error("Nama lengkap wajib diisi")
            return
        }
        if (email.isBlank()) {
            _registerState.value = RegisterUiState.Error("Email wajib diisi")
            return
        }
        if (password.length < 8) {
            _registerState.value = RegisterUiState.Error("Password minimal 8 karakter")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            val result = repository.register(fullName.trim(), email.trim(), phone, password)
            result.onSuccess { response ->
                _registerState.value = RegisterUiState.Success(
                    message = response.message ?: "Pengguna berhasil ditambahkan",
                    userId = response.id
                )
            }.onFailure { error ->
                _registerState.value = RegisterUiState.Error(
                    error.message ?: "Terjadi kesalahan saat pendaftaran"
                )
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginUiState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterUiState.Idle
    }
}
