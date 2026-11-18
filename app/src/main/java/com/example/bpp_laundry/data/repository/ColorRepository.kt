package com.example.bpp_laundry.data.repository

import com.example.bpp_laundry.data.model.ColorResponse
import com.example.bpp_laundry.data.model.UpdateColorRequest
import com.example.bpp_laundry.data.remote.ApiService
import com.example.bpp_laundry.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ColorRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun getColors(): Result<List<ColorResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getColors()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal mengambil data warna (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal terhubung ke Colors API"))
        }
    }

    suspend fun updateColor(
        id: Int,
        name: String,
        color: String,
        isUsed: Boolean?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateColorRequest(name = name, color = color, isUsed = isUsed)
            val response = apiService.updateColor(id = id, request = request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Gagal memperbarui status warna (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal memperbarui warna"))
        }
    }
}
