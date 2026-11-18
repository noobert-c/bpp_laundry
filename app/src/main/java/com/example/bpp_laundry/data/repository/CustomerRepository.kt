package com.example.bpp_laundry.data.repository

import com.example.bpp_laundry.data.model.CreateCustomerRequest
import com.example.bpp_laundry.data.model.CustomerBaseResponse
import com.example.bpp_laundry.data.model.CustomerResponse
import com.example.bpp_laundry.data.remote.ApiService
import com.example.bpp_laundry.data.remote.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun getCustomers(userId: Int = 1): Result<List<CustomerResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCustomers(userId = userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.data.orEmpty())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, CustomerBaseResponse::class.java)
                    parsed?.message ?: "Gagal mengambil data customer (${response.code()})"
                } catch (e: Exception) {
                    "Gagal mengambil data customer (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal terhubung ke Customers API"))
        }
    }

    suspend fun createCustomer(
        name: String,
        phone: String?,
        googleId: String? = null,
        userId: Int = 1
    ): Result<CustomerResponse> = withContext(Dispatchers.IO) {
        try {
            val request = CreateCustomerRequest(
                userId = userId,
                name = name.trim(),
                phone = if (phone.isNullOrBlank()) null else phone.trim(),
                googleId = if (googleId.isNullOrBlank()) null else googleId.trim()
            )
            val response = apiService.createCustomer(request)
            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                Result.success(body.data)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, CustomerBaseResponse::class.java)
                    parsed?.message ?: "Gagal menambahkan customer (${response.code()})"
                } catch (e: Exception) {
                    "Gagal menambahkan customer (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menambahkan customer ke database"))
        }
    }
}
