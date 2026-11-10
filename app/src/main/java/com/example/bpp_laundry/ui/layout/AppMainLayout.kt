
package com.example.bpp_laundry.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bpp_laundry.ui.components.AppTopSpacer
import com.example.bpp_laundry.ui.theme.AppColors

@Composable
fun AppMainLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
    bottomBar: @Composable () -> Unit = {},
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 16.dp,
    contentGap: Dp = 16.dp,
    bottomBarHeight: Dp = 104.dp, // NavBarHeight + AddButtonOverlap
    bottomBarOffset: Dp = 0.dp,
    bottomBarPadding: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.MainBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar background sampai status bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        spotColor = AppColors.TextMain.copy(alpha = 0.10f),
                    )
                    .background(AppColors.Surface)
                    .statusBarsPadding()
            ) {
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
            }

            // Konten utama
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = verticalPadding,
                        bottom = verticalPadding + bottomBarHeight,
                    ),
                verticalArrangement = Arrangement.spacedBy(contentGap),
                content = content
            )
        }

        // Bottom bar mengambang di atas konten
        // Bottom bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .offset(y = -bottomBarOffset)
                .padding(bottomBarPadding),
        ) {
            bottomBar()
        }
    }
}
