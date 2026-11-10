package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.bpp_laundry.dashboard.DashboardTab

private val NavPrimary = Color(0xFF0F766E)
private val NavPrimaryTint = Color(0xFFCCFBF1)
private val NavSurface = Color.White.copy(alpha = 0.86f)
private val NavBorder = Color(0xFFE2E8F0).copy(alpha = 0.7f)
private val NavMuted = Color(0xFF64748B)

private val NavBarHeight = 76.dp
private val AddButtonSize = 56.dp
private val AddButtonOverlap = 28.dp
private val NavBarShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

@Composable
fun AppBottomNavigation(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = DashboardTab.entries
    val splitIndex = tabs.size / 2 // tombol + ditempatkan di tengah

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(NavBarHeight + AddButtonOverlap),
    ) {
        // Navigasi semi-transparan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(NavBarHeight)
                .align(Alignment.BottomCenter)
                .clip(NavBarShape)
                .background(NavSurface)
                .border(width = 1.dp, color = NavBorder, shape = NavBarShape)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, tab ->
                // Ruang untuk tombol + mengambang
                if (index == splitIndex) {
                    Spacer(modifier = Modifier.width(AddButtonSize + 16.dp))
                }

                val selected = tab == selectedTab

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(role = Role.Tab) { onTabSelected(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.CenterVertically,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) NavPrimaryTint else Color.Transparent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = tab.icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(
                                if (selected) NavPrimary else NavMuted
                            ),
                        )
                    }

                    Text(
                        text = tab.label,
                        maxLines = 1,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) NavPrimary else NavMuted,
                    )
                }
            }
        }

        // Tombol + berada di atas navigasi
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
                .size(AddButtonSize)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f),
                )
                .clip(CircleShape)
                .background(NavPrimary)
                .clickable(role = Role.Button, onClick = onAddClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Tambah pesanan",
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}