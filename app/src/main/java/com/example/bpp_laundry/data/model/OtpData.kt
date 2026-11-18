package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class OtpData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("expires_in_seconds")
    val expiresInSeconds: Int? = null
)
