
package com.example.bpp_laundry.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.components.AppTopSpacer
import com.example.bpp_laundry.ui.theme.AppColors

@Composable
fun AppLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
    bottomBar: @Composable () -> Unit = {},
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 16.dp,
    contentGap : Dp = 16.dp
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Surface)
            .statusBarsPadding()
    )  {
        // Top Bar
        AppTopSpacer()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = horizontalPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            topBar()
        }

        // Content: hanya bagian ini yang scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                ),
            verticalArrangement = Arrangement.spacedBy(contentGap),
            content = content
        )

        // Bottom Bar: tetap di bawah area yang tersedia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(
                    horizontal = horizontalPadding
                ).padding(top=12.dp,bottom = 36.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            bottomBar()
        }
    }
}
