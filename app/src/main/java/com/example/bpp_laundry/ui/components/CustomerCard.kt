package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private object CustomerCardColors {
    val Primary = Color(0xFF0F766E)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val Border = Color(0xFFE2E8F0)
    val Muted = Color(0xFF64748B)
    val Text = Color(0xFF0F172A)
}

private val CustomerCardShape = RoundedCornerShape(16.dp)

@Composable
fun CustomerCard(
    name: String,
    phone: String,
    orderCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val orderTotal = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = CustomerCardColors.Primary,
            ),
        ) {
            append(orderCount.toString())
        }
        append(" pesanan")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CustomerCardShape)
            .background(CustomerCardColors.Surface)
            .border(1.dp, CustomerCardColors.Border, CustomerCardShape)
            .clickable(
                onClickLabel = "Lihat $name",
                role = Role.Button,
                onClick = onClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CustomerCardColors.Text,
                ),
            )
            Text(
                text = phone,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = CustomerCardColors.Muted,
                ),
            )
            Text(
                text = orderTotal,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CustomerCardColors.Muted,
                ),
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CustomerCardColors.Background),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = CustomerCardColors.Muted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}