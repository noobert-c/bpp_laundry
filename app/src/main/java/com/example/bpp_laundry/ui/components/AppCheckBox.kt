package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
) {
    val primary = Color(0xFF0F766E)
    val textColor = Color(0xFF111827)
    val mutedColor = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)
    val shape = RoundedCornerShape(6.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) },
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = if (description.isNullOrBlank()) {
            Alignment.CenterVertically
        } else {
            Alignment.Top
        },
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(shape)
                .background(if (checked) primary else Color.White)
                .border(
                    width = 1.dp,
                    color = if (checked) primary else borderColor,
                    shape = shape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Tercentang",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) textColor else mutedColor,
            )

            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = mutedColor,
                )
            }
        }
    }
}