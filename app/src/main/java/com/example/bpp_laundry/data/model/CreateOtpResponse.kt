package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class CreateOtpResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: OtpData? = null
)
