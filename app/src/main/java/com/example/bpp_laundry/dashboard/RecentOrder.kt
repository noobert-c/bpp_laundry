package com.example.bpp_laundry.dashboard

data class RecentOrder(
    val id: String,
    val customer: String,
    val detail: String,
    val status: OrderStatus,
    val price: String,
)