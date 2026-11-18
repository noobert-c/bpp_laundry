package com.example.bpp_laundry.data.repository

import com.example.bpp_laundry.data.remote.ApiService
import com.example.bpp_laundry.data.remote.RetrofitClient

class AppRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    // Tambahkan fungsi repository untuk memanggil endpoint API di sini.
}
