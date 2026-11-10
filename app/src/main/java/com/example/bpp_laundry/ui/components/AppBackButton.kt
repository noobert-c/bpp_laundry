package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import com.example.bpp_laundry.ui.theme.AppColors

@Composable
fun AppBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Kembali"
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(AppColors.Surface)
            .border(1.dp, AppColors.BackButtonStroke, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ChevronLeft,
            contentDescription = contentDescription,
            tint = AppColors.BackIconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}