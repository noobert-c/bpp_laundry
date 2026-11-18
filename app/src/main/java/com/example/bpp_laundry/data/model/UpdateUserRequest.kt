package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("password")
    val password: String
)
