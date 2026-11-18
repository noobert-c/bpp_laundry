package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class UserListResponse(
    @SerializedName("data")
    val data: List<UserData>? = null,

    @SerializedName("message")
    val message: String? = null
)
