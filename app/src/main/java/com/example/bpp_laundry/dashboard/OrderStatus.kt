package com.example.bpp_laundry.dashboard

import androidx.compose.ui.graphics.Color

enum class OrderStatus(
    val label: String,
    val background: Color,
    val content: Color,
) {
    Diterima(
        label = "Diterima",
        background = Color(0xFFDBEAFE),
        content = Color(0xFF1D4ED8),
    ),
    Dicuci(
        label = "Dicuci",
        background = Color(0xFFCCFBF1),
        content = Color(0xFF0F766E),
    ),
    Disetrika(
        label = "Disetrika",
        background = Color(0xFFFEF3C7),
        content = Color(0xFFB45309),
    ),
    Diproses(
        label = "Diproses",
        background = Color(0xFFFEF3C7),
        content = Color(0xFFB45309),
    ),
}