package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.theme.AppColors

@Composable
fun OnBoardingIndicator(
    activeIndex: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(3) { index ->
            val isActive = index == activeIndex

            Box(
                modifier = Modifier
                    .then(
                        if (isActive) {
                            Modifier
                                .width(24.dp)
                                .height(8.dp)
                        } else {
                            Modifier.size(8.dp)
                        }
                    )
                    .background(
                        color = if (isActive) {
                            AppColors.OnBoarding_Active
                        } else {
                            AppColors.OnBoarding_Inactive
                        },
                        shape = if (isActive) {
                            RoundedCornerShape(50)
                        } else {
                            CircleShape
                        }
                    )
            )
        }
    }
}