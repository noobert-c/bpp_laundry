package com.example.bpp_laundry.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bpp_laundry.data.model.UserData
import com.example.bpp_laundry.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    data class OtpSent(val message: String, val email: String, val otpCode: String?) : ForgotPasswordUiState()
    data class OtpVerified(val message: String) : ForgotPasswordUiState()
    data class PasswordResetSuccess(val message: String) : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}

class ForgotPasswordViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    var email: String = ""
        private set

    var currentUser: UserData? = null
        private set

    var otpId: Int? = null
        private set

    var generatedOtpCode: String? = null
        private set

    val maskedEmail: String
        get() {
            val mail = currentUser?.email ?: email
            val atIndex = mail.indexOf('@')
            if (atIndex <= 1) return if (mail.isBlank()) "r***@email.com" else mail
            val name = mail.substring(0, atIndex)
            val domain = mail.substring(atIndex)
            return "${name.first()}***$domain"
        }

    fun requestOtp(inputEmail: String) {
        val cleanEmail = inputEmail.trim()
        if (cleanEmail.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Email wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading

            // 1. Cari user berdasarkan email
            val userResult = repository.findUserByEmail(cleanEmail)
            userResult.onSuccess { user ->
                if (user == null || user.id == null) {
                    _uiState.value = ForgotPasswordUiState.Error("Email tidak terdaftar")
                    return@launch
                }

                currentUser = user
                email = cleanEmail

                // 2. Buat OTP untuk user tersebut
                val otpResult = repository.createOtp(userId = user.id, expiresInSeconds = 300)
                otpResult.onSuccess { otpResponse ->
                    otpId = otpResponse.data?.id
                    generatedOtpCode = otpResponse.data?.code

                    _uiState.value = ForgotPasswordUiState.OtpSent(
                        message = otpResponse.message ?: "Kode OTP berhasil dikirim",
                        email = cleanEmail,
                        otpCode = otpResponse.data?.code
                    )
                }.onFailure { otpError ->
                    _uiState.value = ForgotPasswordUiState.Error(
                        otpError.message ?: "Gagal membuat kode OTP"
                    )
                }
            }.onFailure { userError ->
                _uiState.value = ForgotPasswordUiState.Error(
                    userError.message ?: "Terjadi kesalahan saat memeriksa email"
                )
            }
        }
    }

    fun resendOtp() {
        val user = currentUser
        if (user?.id == null) {
            _uiState.value = ForgotPasswordUiState.Error("Sesi telah berakhir, silakan masukkan email kembali")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            val otpResult = repository.createOtp(userId = user.id, expiresInSeconds = 300)
            otpResult.onSuccess { otpResponse ->
                otpId = otpResponse.data?.id
                generatedOtpCode = otpResponse.data?.code

                _uiState.value = ForgotPasswordUiState.OtpSent(
                    message = "Kode OTP baru berhasil dikirim",
                    email = email,
                    otpCode = otpResponse.data?.code
                )
            }.onFailure { otpError ->
                _uiState.value = ForgotPasswordUiState.Error(
                    otpError.message ?: "Gagal mengirim ulang kode OTP"
                )
            }
        }
    }

    fun verifyOtp(inputOtp: String) {
        val cleanOtp = inputOtp.trim()
        if (cleanOtp.length != 6) {
            _uiState.value = ForgotPasswordUiState.Error("Kode OTP harus terdiri dari 6 digit")
            return
        }

        if (cleanOtp != generatedOtpCode) {
            _uiState.value = ForgotPasswordUiState.Error("Kode OTP salah atau tidak cocok")
            return
        }

        val id = otpId
        val user = currentUser
        if (id == null || user?.id == null) {
            _uiState.value = ForgotPasswordUiState.Error("Data OTP tidak valid, silakan ulangi permintaan OTP")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            val updateResult = repository.updateOtpStatus(otpId = id, userId = user.id, used = true)
            updateResult.onSuccess {
                _uiState.value = ForgotPasswordUiState.OtpVerified("Kode OTP berhasil diverifikasi")
            }.onFailure { error ->
                _uiState.value = ForgotPasswordUiState.Error(
                    error.message ?: "Gagal memverifikasi status OTP"
                )
            }
        }
    }

    fun resetPassword(newPassword: String) {
        if (newPassword.length < 8) {
            _uiState.value = ForgotPasswordUiState.Error("Kata sandi minimal 8 karakter")
            return
        }

        val user = currentUser
        if (user?.id == null) {
            _uiState.value = ForgotPasswordUiState.Error("Sesi telah kedaluwarsa, silakan ulangi proses")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            val updateResult = repository.updateUserPassword(
                userId = user.id,
                fullName = user.fullName ?: "User",
                email = user.email ?: email,
                phone = user.phone,
                newPassword = newPassword
            )
            updateResult.onSuccess {
                _uiState.value = ForgotPasswordUiState.PasswordResetSuccess("Kata sandi berhasil diperbarui")
            }.onFailure { error ->
                _uiState.value = ForgotPasswordUiState.Error(
                    error.message ?: "Gagal mengubah kata sandi"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState.Idle
    }

    fun clearAll() {
        _uiState.value = ForgotPasswordUiState.Idle
        email = ""
        currentUser = null
        otpId = null
        generatedOtpCode = null
    }
}
