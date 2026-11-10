package com.example.bpp_laundry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bpp_laundry.dashboard.DashboardTab
import com.example.bpp_laundry.dashboard.OrderStatus
import com.example.bpp_laundry.dashboard.RecentOrder
import com.example.bpp_laundry.ui.components.AppBottomNavigation
import com.example.bpp_laundry.ui.components.AppOrderCard
import com.example.bpp_laundry.ui.components.DashboardStatCard
import com.example.bpp_laundry.ui.layout.AppMainLayout
import com.example.bpp_laundry.ui.theme.AppColors

@Composable
fun DashboardScreen(
    onOrderClick: (String) -> Unit = {},
    onAddOrderClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {},
) {
    var selectedTab by remember {
        mutableStateOf(DashboardTab.Dashboard)
    }

    val recentOrders = remember {
        listOf(
            RecentOrder(
                id = "#ORD-001",
                customer = "Andi Saputra",
                detail = "Cuci Komplit • 3 kg",
                status = OrderStatus.Dicuci,
                price = "Rp15.000",
            ),
            RecentOrder(
                id = "#ORD-002",
                customer = "Siti Rahma",
                detail = "Cuci + Setrika • 5 kg",
                status = OrderStatus.Diterima,
                price = "Rp25.000",
            ),
            RecentOrder(
                id = "#ORD-003",
                customer = "Budi Santoso",
                detail = "Setrika • 2 kg",
                status = OrderStatus.Disetrika,
                price = "Rp10.000",
            ),
        )
    }

    AppMainLayout(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "BPP Laundry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain,
                )

                IconButton(
                    onClick = onNotificationClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Notifikasi",
                        tint = AppColors.TextMain,
                    )
                }
            }
        },
        content = {
            // Greeting
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Selamat pagi!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMain,
                )

                Text(
                    text = "Berikut ringkasan laundry hari ini.",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary,
                )
            }

            // Ringkasan statistik

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ringkasan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DashboardStatCard(
                        title = "Pesanan",
                        value = "12",
                        prefixIcon = painterResource(R.drawable.ic_dashboard_order),
                        modifier = Modifier.weight(1f),
                    )

                    DashboardStatCard(
                        title = "Diproses",
                        value = "5",
                        prefixIcon = painterResource(R.drawable.ic_dashboard_process),
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DashboardStatCard(
                        title = "Selesai",
                        value = "7",
                        prefixIcon = painterResource(R.drawable.ic_dashboard_ready),
                        modifier = Modifier.weight(1f),
                    )

                    DashboardStatCard(
                        title = "Pendapatan",
                        value = "Rp350rb",
                        prefixIcon = painterResource(R.drawable.ic_dashboard_gain),
                        modifier = Modifier.weight(1f),
                    )
                }
            }


            // Pesanan terbaru
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Pesanan Terbaru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMain,
                )

                recentOrders.forEach { order ->
                    AppOrderCard(
                        order = order,
                        onDetailClick = onOrderClick,
                    )
                }
            }
        },
        bottomBar = {
            AppBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    onTabSelected(tab)
                },
                onAddClick = onAddOrderClick,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}

@Composable
private fun DashboardStat(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = AppColors.TextSecondary,
            )

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMain,
            )
        }
    }
}