package com.example.bpp_laundry.data.model

import com.google.gson.annotations.SerializedName

data class UpdateColorRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("is_used")
    val isUsed: Boolean? = null
)
