package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class CheckPointRequest(
    @SerializedName("color_id")
    val colorId: Int,

    @SerializedName("name")
    val name: String
)
