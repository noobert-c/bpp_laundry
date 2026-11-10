package com.example.bpp_laundry.dashboard

import androidx.annotation.DrawableRes
import com.example.bpp_laundry.R

enum class DashboardTab(
    val label: String,
    @DrawableRes val icon: Int,
) {
    Dashboard(
        label = "Dashboard",
        icon = R.drawable.ic_home,
    ),
    Pesanan(
        label = "Pesanan",
        icon = R.drawable.ic_order,
    ),
    Pelanggan(
        label = "Pelanggan",
        icon = R.drawable.ic_customer,
    ),
    Laporan(
        label = "Laporan",
        icon = R.drawable.ic_chart,
    ),
}