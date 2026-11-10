package com.example.bpp_laundry.ui.components




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.bpp_laundry.ui.theme.AppText

@Composable

fun AppTextBlock(
    header: String,
    body: String,
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = header,
            style = AppText.Header,
        )

        Text(
            text = body,
            style = AppText.Body
        )
    }
}
