package com.example.bpp_laundry.data.remote

import com.example.bpp_laundry.data.model.BaseResponse
import com.example.bpp_laundry.data.model.CheckPointRequest
import com.example.bpp_laundry.data.model.CheckPointResponse
import com.example.bpp_laundry.data.model.ColorResponse
import com.example.bpp_laundry.data.model.CreateCustomerRequest
import com.example.bpp_laundry.data.model.CreateOtpRequest
import com.example.bpp_laundry.data.model.CreateOtpResponse
import com.example.bpp_laundry.data.model.CreatePackageCheckpointRequest
import com.example.bpp_laundry.data.model.CreatePackageRequest
import com.example.bpp_laundry.data.model.CustomerBaseResponse
import com.example.bpp_laundry.data.model.CustomerResponse
import com.example.bpp_laundry.data.model.LoginRequest
import com.example.bpp_laundry.data.model.LoginResponse
import com.example.bpp_laundry.data.model.PackageBaseResponse
import com.example.bpp_laundry.data.model.PackageCheckpointItem
import com.example.bpp_laundry.data.model.PackageResponse
import com.example.bpp_laundry.data.model.RegisterRequest
import com.example.bpp_laundry.data.model.RegisterResponse
import com.example.bpp_laundry.data.model.UpdateColorRequest
import com.example.bpp_laundry.data.model.UpdateOtpStatusRequest
import com.example.bpp_laundry.data.model.UpdatePackageCheckpointRequest
import com.example.bpp_laundry.data.model.UpdatePackageRequest
import com.example.bpp_laundry.data.model.UpdateUserRequest
import com.example.bpp_laundry.data.model.UserListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("bpplaundry/database/app-login/auth-user.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("bpplaundry/database/app-login/users.php")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("bpplaundry/database/app-login/users.php")
    suspend fun getAllUsers(): Response<UserListResponse>

    @Headers("Content-Type: application/json")
    @PUT("bpplaundry/database/app-login/users.php")
    suspend fun updateUser(
        @Query("id") id: Int,
        @Body request: UpdateUserRequest
    ): Response<BaseResponse<Unit>>

    @Headers("Content-Type: application/json")
    @POST("bpplaundry/database/otp/otp_codes.php")
    suspend fun createOtp(
        @Body request: CreateOtpRequest
    ): Response<CreateOtpResponse>

    @Headers("Content-Type: application/json")
    @PUT("bpplaundry/database/otp/otp_codes.php")
    suspend fun updateOtpStatus(
        @Query("id") id: Int,
        @Query("user_id") userId: Int,
        @Body request: UpdateOtpStatusRequest
    ): Response<BaseResponse<Unit>>

    // Colors API
    @GET("bpplaundry/database/checkpoints/colors.php")
    suspend fun getColors(): Response<List<ColorResponse>>

    @GET("bpplaundry/database/checkpoints/colors.php")
    suspend fun getColorById(
        @Query("id") id: Int
    ): Response<ColorResponse>

    @Headers("Content-Type: application/json")
    @PUT("bpplaundry/database/checkpoints/colors.php")
    suspend fun updateColor(
        @Query("id") id: Int,
        @Body request: UpdateColorRequest
    ): Response<BaseResponse<Unit>>

    // Checkpoints API
    @GET("bpplaundry/database/checkpoints/checkpoints.php")
    suspend fun getCheckpoints(): Response<List<CheckPointResponse>>

    @GET("bpplaundry/database/checkpoints/checkpoints.php")
    suspend fun getCheckpointById(
        @Query("id") id: Int
    ): Response<CheckPointResponse>

    @Headers("Content-Type: application/json")
    @POST("bpplaundry/database/checkpoints/checkpoints.php")
    suspend fun createCheckpoint(
        @Body request: CheckPointRequest
    ): Response<BaseResponse<Unit>>

    @Headers("Content-Type: application/json")
    @PUT("bpplaundry/database/checkpoints/checkpoints.php")
    suspend fun updateCheckpoint(
        @Query("id") id: Int,
        @Body request: CheckPointRequest
    ): Response<BaseResponse<Unit>>

    @DELETE("bpplaundry/database/checkpoints/checkpoints.php")
    suspend fun deleteCheckpoint(
        @Query("id") id: Int
    ): Response<BaseResponse<Unit>>

    // Packages API
    @GET("bpplaundry/database/packages/packages.php")
    suspend fun getPackages(
        @Query("id") id: Int? = null
    ): Response<PackageBaseResponse<List<PackageResponse>>>

    @Headers("Content-Type: application/json")
    @POST("bpplaundry/database/packages/packages.php")
    suspend fun createPackage(
        @Body request: CreatePackageRequest
    ): Response<PackageBaseResponse<PackageResponse>>

    @Headers("Content-Type: application/json")
    @PUT("bpplaundry/database/packages/packages.php")
    suspend fun updatePackage(
        @Body request: UpdatePackageRequest
    ): Response<PackageBaseResponse<PackageResponse>>

    @DELETE("bpplaundry/database/packages/packages.php")
    suspend fun deletePackage(
        @Query("id") id: Int
    ): Response<PackageBaseResponse<Unit>>

    // Package Checkpoints API
    @GET("bpplaundry/database/packages/package_checkpoints.php")
    suspend fun getPackageCheckpoints(
        @Query("id") id: Int? = null,
        @Query("package_id") packageId: Int? = null,
        @Query("checkpoint_id") checkpointId: Int? = null
    ): Response<PackageBaseResponse<List<PackageCheckpointItem>>>

    @Headers("Content-Type: application/json")
    @POST("bpplaundry/database/packages/package_checkpoints.php")
    suspend fun createPackageCheckpoint(
        @Body request: CreatePackageCheckpointRequest
    ): Response<PackageBaseResponse<PackageCheckpointItem>>

    @Headers("Content-Type: application/json")
    @PUT("bpplaundry/database/packages/package_checkpoints.php")
    suspend fun updatePackageCheckpoint(
        @Body request: UpdatePackageCheckpointRequest
    ): Response<PackageBaseResponse<PackageCheckpointItem>>

    @DELETE("bpplaundry/database/packages/package_checkpoints.php")
    suspend fun deletePackageCheckpoint(
        @Query("id") id: Int
    ): Response<PackageBaseResponse<Unit>>

    // Customers API
    @GET("bpplaundry/database/customers/customers.php")
    suspend fun getCustomers(
        @Query("user_id") userId: Int,
        @Query("id") id: Int? = null
    ): Response<CustomerBaseResponse<List<CustomerResponse>>>

    @Headers("Content-Type: application/json")
    @POST("bpplaundry/database/customers/customers.php")
    suspend fun createCustomer(
        @Body request: CreateCustomerRequest
    ): Response<CustomerBaseResponse<CustomerResponse>>
}
