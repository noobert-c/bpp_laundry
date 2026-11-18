
package com.example.laundryflow.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.ui.theme.AppColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PackageListItem(
    name: String,
    pricePerKg: Int,
    estimatedDays: Int,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val textSub = Color(0xFF64748B)
    val danger = Color(0xFFDC2626)

    val formattedPrice = NumberFormat
        .getNumberInstance(Locale("id", "ID"))
        .format(pricePerKg)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                AppColors.BorderApp,
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Nama dan harga
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Rp $formattedPrice/kg",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.PrimaryApp
                )
            }

            // Estimasi dan tombol aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Estimasi",
                    tint = textSub,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = "$estimatedDays hari",
                    fontSize = 12.sp,
                    color = textSub
                )

                Spacer(Modifier.weight(1f))

                if (onEdit != null) {
                    TextButton(
                        onClick = onEdit,
                        contentPadding = PaddingValues(
                            horizontal = 6.dp,
                            vertical = 0.dp
                        ),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text("Edit", fontSize = 12.sp)
                    }
                }

                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        contentPadding = PaddingValues(
                            horizontal = 6.dp,
                            vertical = 0.dp
                        ),
                        modifier = Modifier.height(30.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = danger
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text("Hapus", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
