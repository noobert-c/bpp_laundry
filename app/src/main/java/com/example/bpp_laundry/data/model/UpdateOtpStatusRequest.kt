package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class UpdateOtpStatusRequest(
    @SerializedName("used")
    val used: Boolean
)
