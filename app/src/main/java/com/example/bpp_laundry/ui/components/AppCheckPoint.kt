
package com.example.laundryflow.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.ui.components.AppStatusBadge

// MODEL
enum class CheckPointColor(val label: String, val color: Color) {
    BIRU("Biru", Color(0xFF3B82F6)),
    HIJAU("Hijau", Color(0xFF22C55E)),
    ORANYE("Oranye", Color(0xFFF97316)),
    UNGU("Ungu", Color(0xFF8B5CF6))
}

data class CheckPoint(
    val id: Int,
    val name: String,
    val color: Color
)

// WARNA
private val Outline = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextSub = Color(0xFF64748B)
private val Danger = Color(0xFFDC2626)

// COLOR DOT
@Composable
fun CheckPointColorDot(
    color: Color,
    size: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
    )
}

// IDENTITAS CHECK POINT
@Composable
fun CheckPointIdentity(
    item: CheckPoint,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(item.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            CheckPointColorDot(item.color, 18)
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                item.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextMain,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// CARD CHECK POINT



@Composable
fun CheckPointCard(
    item: CheckPoint,
    onEdit: (CheckPoint) -> Unit,
    onDelete: (CheckPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFDEE2E6)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Badge nama check point
            AppStatusBadge(
                text = item.name,
                backgroundColor = item.color.copy(alpha = 0.15f),
                textColor = item.color,
                showDot = true
            )

            Spacer(Modifier.weight(1f))

            // Tombol Edit
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit
                IconButton(
                    onClick = { onEdit(item) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF4151AF),
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Hapus
                IconButton(
                    onClick = { onDelete(item) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color(0xFFDC3545),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}




// TOMBOL AKSI
@Composable
private fun CheckPointAction(
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            Modifier.size(12.dp),
            tint = tint
        )

        Spacer(Modifier.width(4.dp))

        Text(
            label,
            fontSize = 12.sp,
            color = tint,
            fontWeight = FontWeight.Medium
        )
    }
}
