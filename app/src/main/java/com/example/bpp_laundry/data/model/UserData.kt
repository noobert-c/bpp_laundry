package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("full_name")
    val fullName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)
