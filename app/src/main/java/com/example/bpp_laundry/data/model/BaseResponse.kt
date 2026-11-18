package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

/**
 * Template umum response pembungkus dari backend (opsional).
 */
data class BaseResponse<T>(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null
)
