
package com.example.bpp_laundry.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.theme.AppColors
import com.example.bpp_laundry.ui.theme.AppText

@Composable
fun AppChip(
    text: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppColors.chip,
    contentColor: Color = AppColors.TextMain,
    strokeColor: Color = AppColors.chip_stroke,
    iconTint: Color = Color.Unspecified,
    iconBackgroundColor: Color = Color.Transparent
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, strokeColor, CircleShape)
            .background(backgroundColor)
            .padding(
                start = 6.dp,
                top = 6.dp,
                end = 14.dp,
                bottom = 6.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = if (iconTint == Color.Unspecified) {
                    null
                } else {
                    ColorFilter.tint(iconTint)
                }
            )
        }

        Text(
            text = text,
            style = AppText.Chip,
            color = contentColor
        )
    }
}
