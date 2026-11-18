package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class CreateOtpRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("expires_in_seconds")
    val expiresInSeconds: Int = 300
)
