package com.example.bpp_laundry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bpp_laundry.navigation.AppNavigation
import com.example.bpp_laundry.DashboardScreen
import com.example.bpp_laundry.ui.layout.AppLayout
import com.example.bpp_laundry.ui.preview.*
import com.example.bpp_laundry.ui.screens.WhatsAppSettingsScreen
import com.example.laundryflow.AkunFeature
import com.example.laundryflow.AkunTheme

import com.example.laundryflow.CheckPointScreen
import com.example.laundryflow.PaketHargaFeature


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddOrderScreen()
                }
            }
        }
    }
}