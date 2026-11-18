package com.example.bpp_laundry.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    /**
     * CATATAN UNTUK KONEKSI KE SERVER LOKAL:
     * - Emulator Android Studio:
     *   "http://10.0.2.2/" (jika server Apache XAMPP default di port 80)
     *   atau "http://10.0.2.2:8000/" (jika pakai port 8000, php -S localhost:8000)
     * - Real Device (HP Fisik):
     *   Gunakan IP Laptop/PC Anda pada jaringan Wi-Fi yang sama,
     *   contoh: "http://192.168.1.15/" atau "http://192.168.1.15:8000/"
     */
    private const val BASE_URL = "http://192.168.1.11/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
