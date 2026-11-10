package com.example.bpp_laundry.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.components.AppTopSpacer

@Composable
fun AuthLayout(
    modifier: Modifier = Modifier,
    gap: androidx.compose.ui.unit.Dp = 32.dp,
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(
                    start = 16.dp,
                    top = 8.dp,
                    end = 16.dp,
                    bottom = 0.dp
                )
        ) {
            AppTopSpacer()
            topBar()
        }

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    start = 16.dp,
                    top = 8.dp,
                    end = 16.dp,
                    bottom = 0.dp
                ),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            content()
        }

        // BOTTOM BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(
                    start = 16.dp,
                    top = 0.dp,
                    end = 16.dp,
                    bottom = 36.dp
                )
        ) {
            bottomBar()
        }
    }
}