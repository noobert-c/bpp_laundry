package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class PackageBaseResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null
)

data class PackageResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price_per_kg")
    val pricePerKg: Double,

    @SerializedName("est_days")
    val estDays: Int
)

data class CreatePackageRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("price_per_kg")
    val pricePerKg: Double,

    @SerializedName("est_days")
    val estDays: Int
)

data class UpdatePackageRequest(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price_per_kg")
    val pricePerKg: Double,

    @SerializedName("est_days")
    val estDays: Int
)

data class PackageCheckpointItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("package_id")
    val packageId: Int,

    @SerializedName("package_name")
    val packageName: String? = null,

    @SerializedName("checkpoint_id")
    val checkpointId: Int,

    @SerializedName("checkpoint_name")
    val checkpointName: String? = null
)

data class CreatePackageCheckpointRequest(
    @SerializedName("package_id")
    val packageId: Int,

    @SerializedName("checkpoint_id")
    val checkpointId: Int
)

data class UpdatePackageCheckpointRequest(
    @SerializedName("id")
    val id: Int,

    @SerializedName("package_id")
    val packageId: Int,

    @SerializedName("checkpoint_id")
    val checkpointId: Int
)
