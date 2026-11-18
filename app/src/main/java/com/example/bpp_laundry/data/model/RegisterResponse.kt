package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("id")
    val id: Int? = null
)
