package com.example.bpp_laundry.data.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

data class ColorResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("is_used")
    val isUsed: Boolean = false
) {
    /**
     * Mengonversi string color (format: 0xFFRRGGBB, #RRGGBB, atau RRGGBB)
     * ke Compose Color.
     */
    fun toComposeColor(defaultColor: Color = Color(0xFF3B82F6)): Color {
        return try {
            val clean = color.trim()
            val hex = when {
                clean.startsWith("0x", ignoreCase = true) -> clean.substring(2)
                clean.startsWith("#") -> clean.substring(1)
                else -> clean
            }
            val fullHex = if (hex.length == 6) "FF$hex" else hex
            Color(fullHex.toLong(16))
        } catch (e: Exception) {
            defaultColor
        }
    }
}
