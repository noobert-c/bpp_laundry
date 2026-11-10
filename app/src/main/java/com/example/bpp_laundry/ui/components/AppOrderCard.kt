
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.dashboard.*

private val CardSurface = Color.White
private val CardBorder = Color(0xFFE2E8F0)
private val CardText = Color(0xFF0F172A)
private val CardMuted = Color(0xFF64748B)
private val CardBackground = Color(0xFFF8FAFC)
private val CardPrimary = Color(0xFF0F766E)
private val CardShape = RoundedCornerShape(16.dp)

@Composable
fun AppOrderCard(
    order: RecentOrder,
    onDetailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(CardSurface)
            .border(1.dp, CardBorder, CardShape)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = order.id,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = CardMuted,
            )

            Text(
                text = order.customer,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = CardText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = order.detail,
                fontSize = 12.sp,
                color = CardMuted,
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
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = CardPrimary,
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(CardBackground)
                .clickable(
                    role = Role.Button,
                    onClick = { onDetailClick(order.id) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Detail pesanan ${order.id}",
                tint = CardMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun OrderStatusBadge(status: OrderStatus) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(status.background)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(status.content),
        )

        Text(
            text = status.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = status.content,
        )
    }
}
