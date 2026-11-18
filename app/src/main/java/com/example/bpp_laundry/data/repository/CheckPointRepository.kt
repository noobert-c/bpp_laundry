package com.example.bpp_laundry.data.repository

import com.example.bpp_laundry.data.model.BaseResponse
import com.example.bpp_laundry.data.model.CheckPointRequest
import com.example.bpp_laundry.data.model.CheckPointResponse
import com.example.bpp_laundry.data.remote.ApiService
import com.example.bpp_laundry.data.remote.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckPointRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun getCheckpoints(): Result<List<CheckPointResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCheckpoints()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal mengambil data checkpoint (${response.code()})"
                } catch (e: Exception) {
                    "Gagal mengambil data checkpoint (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal terhubung ke Checkpoints API"))
        }
    }

    suspend fun createCheckpoint(colorId: Int, name: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = CheckPointRequest(colorId = colorId, name = name)
            val response = apiService.createCheckpoint(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal menambahkan checkpoint (${response.code()})"
                } catch (e: Exception) {
                    "Gagal menambahkan checkpoint (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menambahkan checkpoint"))
        }
    }

    suspend fun updateCheckpoint(id: Int, colorId: Int, name: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = CheckPointRequest(colorId = colorId, name = name)
            val response = apiService.updateCheckpoint(id = id, request = request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal memperbarui checkpoint (${response.code()})"
                } catch (e: Exception) {
                    "Gagal memperbarui checkpoint (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal memperbarui checkpoint"))
        }
    }

    suspend fun deleteCheckpoint(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteCheckpoint(id = id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, BaseResponse::class.java)
                    parsed?.message ?: "Gagal menghapus checkpoint (${response.code()})"
                } catch (e: Exception) {
                    "Gagal menghapus checkpoint (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menghapus checkpoint"))
        }
    }
}
