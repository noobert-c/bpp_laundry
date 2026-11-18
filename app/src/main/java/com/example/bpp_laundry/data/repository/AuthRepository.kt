package com.example.bpp_laundry.data.repository

import com.example.bpp_laundry.data.model.BaseResponse
import com.example.bpp_laundry.data.model.CreateOtpRequest
import com.example.bpp_laundry.data.model.CreateOtpResponse
import com.example.bpp_laundry.data.model.LoginRequest
import com.example.bpp_laundry.data.model.LoginResponse
import com.example.bpp_laundry.data.model.RegisterRequest
import com.example.bpp_laundry.data.model.RegisterResponse
import com.example.bpp_laundry.data.model.UpdateOtpStatusRequest
import com.example.bpp_laundry.data.model.UpdateUserRequest
import com.example.bpp_laundry.data.model.UserData
import com.example.bpp_laundry.data.remote.ApiService
import com.example.bpp_laundry.data.remote.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun login(email: String, password: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email = email, password = password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, LoginResponse::class.java)
                    parsed?.message ?: "Gagal login (${response.code()})"
                } catch (e: Exception) {
                    "Gagal login (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(e.localizedMessage ?: "Tidak dapat terhubung ke server lokal. Pastikan server aktif.")
            )
        }
    }

    suspend fun register(
        fullName: String,
        email: String,
        phone: String?,
        password: String
    ): Result<RegisterResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(
                fullName = fullName,
                email = email,
                phone = if (phone.isNullOrBlank()) null else phone.trim(),
                password = password
            )
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, RegisterResponse::class.java)
                    parsed?.message ?: "Gagal mendaftar (${response.code()})"
                } catch (e: Exception) {
                    "Gagal mendaftar (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(e.localizedMessage ?: "Tidak dapat terhubung ke server lokal. Pastikan server aktif.")
            )
        }
    }

    suspend fun findUserByEmail(email: String): Result<UserData?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                val users = response.body()?.data.orEmpty()
                val found = users.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
                Result.success(found)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal mengambil data user (${response.code()})"
                } catch (e: Exception) {
                    "Gagal mengambil data user (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(e.localizedMessage ?: "Tidak dapat terhubung ke server lokal.")
            )
        }
    }

    suspend fun createOtp(userId: Int, expiresInSeconds: Int = 300): Result<CreateOtpResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createOtp(CreateOtpRequest(userId = userId, expiresInSeconds = expiresInSeconds))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal membuat kode OTP (${response.code()})"
                } catch (e: Exception) {
                    "Gagal membuat kode OTP (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(e.localizedMessage ?: "Gagal membuat kode OTP.")
            )
        }
    }

    suspend fun updateOtpStatus(otpId: Int, userId: Int, used: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateOtpStatus(id = otpId, userId = userId, request = UpdateOtpStatusRequest(used = used))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal memperbarui status OTP (${response.code()})"
                } catch (e: Exception) {
                    "Gagal memperbarui status OTP (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(e.localizedMessage ?: "Gagal memverifikasi OTP.")
            )
        }
    }

    suspend fun updateUserPassword(
        userId: Int,
        fullName: String,
        email: String,
        phone: String?,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateUserRequest(
                fullName = fullName,
                email = email,
                phone = phone,
                password = newPassword
            )
            val response = apiService.updateUser(id = userId, request = request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal memperbarui kata sandi (${response.code()})"
                } catch (e: Exception) {
                    "Gagal memperbarui kata sandi (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(e.localizedMessage ?: "Gagal memperbarui kata sandi.")
            )
        }
    }
}
