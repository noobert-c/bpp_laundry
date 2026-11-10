
package com.example.bpp_laundry.ui.layout
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
@Composable
fun OnBoardingLayout(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFF5F7F8),
    horizontalPadding: Dp = 24.dp,
    // Independent padding
    topBarPadding: PaddingValues = PaddingValues(top = 44.dp, start = horizontalPadding, end = horizontalPadding),
    contentPadding: PaddingValues = PaddingValues(horizontal = horizontalPadding),
    bottomBarPadding: PaddingValues = PaddingValues(bottom = 44.dp, start = horizontalPadding, end = horizontalPadding),

    topBar: (@Composable () -> Unit)? = null,
    bottomButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor,

        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = topBarPadding.calculateTopPadding()
                    )
                    .height(54.dp)
                    .padding(
                        start = topBarPadding.calculateStartPadding(
                            androidx.compose.ui.unit.LayoutDirection.Ltr
                        ),
                        end = topBarPadding.calculateEndPadding(
                            androidx.compose.ui.unit.LayoutDirection.Ltr
                        )
                    )
            ) {
                topBar?.invoke()
            }
        },

        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottomBarPadding)
            ) {
                bottomButton()
            }
        }
    )  { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize().offset(x=0.dp,y=0.dp)
                .padding(innerPadding)
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
