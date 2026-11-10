
package com.example.bpp_laundry.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.R

val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold)
)

object AppText {

    private fun s(
        size: Int,
        weight: FontWeight,
        color: androidx.compose.ui.graphics.Color,
        lineHeight: Int? = null
    ) = TextStyle(
        fontFamily = InterFamily,
        fontSize = size.sp,
        fontWeight = weight,
        color = color,
        lineHeight = lineHeight?.sp
            ?: androidx.compose.ui.unit.TextUnit.Unspecified
    )

    val Header = s(
        24,
        FontWeight.Bold,
        AppColors.TextMain
    )

    val Body = s(
        14,
        FontWeight.Normal,
        AppColors.TextSecondary,
        lineHeight = 22
    )

    val Footer = s(
        12,
        FontWeight.Normal,
        AppColors.TextSecondary
    )

    val Chip = s(
        12,
        FontWeight.Medium,
        AppColors.TextMain
    )

    val Button = s(
        14,
        FontWeight.SemiBold,
        AppColors.OnPrimary
    )

    val Input_Label = s(
        13,
        FontWeight.Medium,
        AppColors.TextSecondary
    )

    val Input_Text = s(
        14,
        FontWeight.Normal,
        AppColors.TextMain
    )

    val TextButton = s(
        13,
        FontWeight.SemiBold,
        AppColors.PrimaryApp
    )

    val TotalBiaya = s(
        14,
        FontWeight.Normal,
        AppColors.TextMain
    )

     val BiayaUi = s(
         16,
         FontWeight.Bold,
         AppColors.PrimaryApp
     )

}
