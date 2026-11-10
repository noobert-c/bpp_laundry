package com.example.bpp_laundry.ui.theme

import androidx.compose.ui.graphics.Color

/** Token warna dari spesifikasi YAML. Satu-satunya tempat angka warna ditulis. */
object AppColors {
    // Teks
    val TextMain = Color(0xFF111827)
    val TextSecondary = Color(0xFF6B7280)
    val TextAccent = Color(0xFF0F7B6C)
    val OnPrimary = Color(0xFFFFFFFF)
    val StatusBarApp = Color(0xFF0F172A)
    val NavInactive = Color(0xFF64748B)


    // Permukaan & border
    val Surface = Color(0xFFFFFFFF)
    val MainBackground = Color(0xFFF8FAFC)
    val BackgroundAuth = Color(0xFFF6F8FA)
    val BorderAuth = Color(0xFFDDE7E6)
    val BorderApp = Color(0xFFE2E8F0)

    // Primary — ada dua gaya di desain, perlu diputuskan satu.
    val PrimaryAuth = Color(0xFF0F7B6C)
    val PrimaryApp = Color(0xFF0F766E)

    // Latar ikon chip / badge
    val SoftGreen = Color(0xFFD1F5EC)
    val SoftAmber = Color(0xFFFEF3C7)
    val SoftBlue = Color(0xFFDBEAFE)
    val OnSoftGreen = PrimaryAuth
    val OnSoftAmber = Color(0xFFB45309) // asumsi, tidak ada di YAML
    val OnSoftBlue = Color(0xFF2563EB)




    //chip
    val chip =  OnPrimary
    val chip_stroke = Color(0xFFDDE7E6)



    //onboarding
    val OnBoarding_Inactive = Color(0xFFDCE5E5)
    val OnBoarding_Active = PrimaryApp

    //back button
    val BackIconColor = Color(0xFF0F172A)
    val BackButtonStroke = Color(0xFFE2E8F0)

    //input error
    val ErrorInput = Color(0xFFB00020)

}
