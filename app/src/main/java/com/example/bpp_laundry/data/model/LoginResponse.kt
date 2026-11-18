package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: UserData? = null
)
