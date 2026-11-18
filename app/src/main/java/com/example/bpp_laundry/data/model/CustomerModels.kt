package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class CustomerBaseResponse<T>(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null
)

data class CustomerResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("google_id")
    val googleId: String? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)

data class CreateCustomerRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("google_id")
    val googleId: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String? = null
)
