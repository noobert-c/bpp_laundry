package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class CheckPointResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("color_id")
    val colorId: Int,

    @SerializedName("name")
    val name: String
)
