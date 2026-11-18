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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.dashboard.*
import com.example.bpp_laundry.ui.theme.InterFamily

private val AppOrderCardSurface = Color(0xFFFFFFFF)
private val AppOrderCardBorder = Color(0xFFE2E8F0)
private val AppOrderCardText = Color(0xFF0F172A)
private val AppOrderCardMuted = Color(0xFF64748B)
private val AppOrderCardBackground = Color(0xFFF8FAFC)
private val AppOrderCardPrimary = Color(0xFF0F766E)
private val AppOrderCardShape = RoundedCornerShape(16.dp)

@Composable
fun AppOrderCard(
    order: RecentOrder,
    onDetailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppOrderCardShape)
            .background(AppOrderCardSurface)
            .border(1.dp, AppOrderCardBorder, AppOrderCardShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = order.id,
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = AppOrderCardMuted,
                ),
            )
            Text(
                text = order.customer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = AppOrderCardText,
                ),
            )
            Text(
                text = order.detail,
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = AppOrderCardMuted,
                ),
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppStatusBadge(
                text = order.status.label,
                backgroundColor = order.status.background,
                textColor = order.status.content,
            )
            Text(
                text = order.price,
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = AppOrderCardPrimary,
                ),
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(AppOrderCardBackground)
                .clickable(
                    role = Role.Button,
                    onClick = { onDetailClick(order.id) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Lihat detail pesanan ${order.id}",
                tint = AppOrderCardMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}