package com.example.bpp_laundry.data.repository

import com.example.bpp_laundry.data.model.CreatePackageCheckpointRequest
import com.example.bpp_laundry.data.model.CreatePackageRequest
import com.example.bpp_laundry.data.model.PackageBaseResponse
import com.example.bpp_laundry.data.model.PackageCheckpointItem
import com.example.bpp_laundry.data.model.PackageResponse
import com.example.bpp_laundry.data.model.UpdatePackageRequest
import com.example.bpp_laundry.data.remote.ApiService
import com.example.bpp_laundry.data.remote.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PackageRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun getPackages(): Result<List<PackageResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPackages()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.orEmpty())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, PackageBaseResponse::class.java)
                    parsed?.message ?: "Gagal mengambil data paket (${response.code()})"
                } catch (e: Exception) {
                    "Gagal mengambil data paket (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal terhubung ke Packages API"))
        }
    }

    suspend fun createPackage(
        name: String,
        pricePerKg: Double,
        estDays: Int
    ): Result<PackageResponse> = withContext(Dispatchers.IO) {
        try {
            val request = CreatePackageRequest(name = name, pricePerKg = pricePerKg, estDays = estDays)
            val response = apiService.createPackage(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, PackageBaseResponse::class.java)
                    parsed?.message ?: "Gagal membuat paket (${response.code()})"
                } catch (e: Exception) {
                    "Gagal membuat paket (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menambahkan paket"))
        }
    }

    suspend fun updatePackage(
        id: Int,
        name: String,
        pricePerKg: Double,
        estDays: Int
    ): Result<PackageResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdatePackageRequest(id = id, name = name, pricePerKg = pricePerKg, estDays = estDays)
            val response = apiService.updatePackage(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, PackageBaseResponse::class.java)
                    parsed?.message ?: "Gagal memperbarui paket (${response.code()})"
                } catch (e: Exception) {
                    "Gagal memperbarui paket (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal memperbarui paket"))
        }
    }

    suspend fun deletePackage(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deletePackage(id = id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, PackageBaseResponse::class.java)
                    parsed?.message ?: "Gagal menghapus paket (${response.code()})"
                } catch (e: Exception) {
                    "Gagal menghapus paket (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menghapus paket"))
        }
    }

    suspend fun getPackageCheckpoints(
        packageId: Int? = null
    ): Result<List<PackageCheckpointItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPackageCheckpoints(packageId = packageId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.orEmpty())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, PackageBaseResponse::class.java)
                    parsed?.message ?: "Gagal mengambil data check point paket (${response.code()})"
                } catch (e: Exception) {
                    "Gagal mengambil data check point paket (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal terhubung ke Package Checkpoints API"))
        }
    }

    suspend fun createPackageCheckpoint(
        packageId: Int,
        checkpointId: Int
    ): Result<PackageCheckpointItem> = withContext(Dispatchers.IO) {
        try {
            val request = CreatePackageCheckpointRequest(packageId = packageId, checkpointId = checkpointId)
            val response = apiService.createPackageCheckpoint(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, PackageBaseResponse::class.java)
                    parsed?.message ?: "Gagal menambahkan relasi checkpoint (${response.code()})"
                } catch (e: Exception) {
                    "Gagal menambahkan relasi checkpoint (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menambahkan checkpoint ke paket"))
        }
    }

    suspend fun deletePackageCheckpoint(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deletePackageCheckpoint(id = id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    val parsed = Gson().fromJson(errorBody, PackageBaseResponse::class.java)
                    parsed?.message ?: "Gagal menghapus relasi checkpoint (${response.code()})"
                } catch (e: Exception) {
                    "Gagal menghapus relasi checkpoint (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menghapus checkpoint dari paket"))
        }
    }

    suspend fun syncPackageCheckpoints(
        packageId: Int,
        newCheckpointIds: List<Int>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Ambil relasi yang sudah ada untuk package ini
            val existingRes = apiService.getPackageCheckpoints(packageId = packageId)
            val existingRelations = if (existingRes.isSuccessful && existingRes.body() != null) {
                existingRes.body()!!.data.orEmpty()
            } else {
                emptyList()
            }

            // Hapus relasi yang tidak ada di newCheckpointIds
            for (rel in existingRelations) {
                if (!newCheckpointIds.contains(rel.checkpointId)) {
                    apiService.deletePackageCheckpoint(id = rel.id)
                }
            }

            // Tambahkan checkpoint baru yang belum ada
            val existingCpIds = existingRelations.map { it.checkpointId }.toSet()
            for (cpId in newCheckpointIds) {
                if (!existingCpIds.contains(cpId)) {
                    apiService.createPackageCheckpoint(
                        CreatePackageCheckpointRequest(packageId = packageId, checkpointId = cpId)
                    )
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menyelaraskan check point paket"))
        }
    }
}
