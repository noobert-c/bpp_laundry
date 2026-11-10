
package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.bpp_laundry.ui.theme.AppColors
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material3.Icon

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    prefixIcon: Painter? = null,
    border: BorderStroke? = BorderStroke(
        width = 1.dp,
        color = AppColors.BorderApp
    ),
) {
    Card(
        modifier = modifier,
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        )
    )  {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (prefixIcon != null) {
                    Icon(
                        painter = prefixIcon,
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }

                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary,
                )
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain,
            )
        }
    }
}
