
package com.example.bpp_laundry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val OtpDigitShape = RoundedCornerShape(12.dp)

@Composable
fun AppOtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    digitCount: Int = 6,
    focusRequester: FocusRequester? = null,
    onDone: () -> Unit = {},
    primaryColor: Color = Color(0xFF0F7B6C),
    textColor: Color = Color(0xFF111827),
    backgroundColor: Color = Color.White,
    borderColor: Color = Color(0xFFDDE7E6),
    digitHeight: androidx.compose.ui.unit.Dp = 48.dp,
    digitGap: androidx.compose.ui.unit.Dp = 8.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 12.dp,
) {
    var focused by remember { mutableStateOf(false) }
    var cursorOn by remember { mutableStateOf(true) }

    LaunchedEffect(focused) {
        cursorOn = true
        while (focused) {
            delay(530)
            cursorOn = !cursorOn
        }
    }

    BasicTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(
                input.filter { it.isDigit() }.take(digitCount)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                } else {
                    Modifier
                }
            )
            .onFocusChanged { focused = it.isFocused }
            .semantics {
                contentDescription = "Kode OTP, $digitCount digit"
            },
        singleLine = true,
        textStyle = TextStyle(color = Color.Transparent),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(digitGap),
            ) {
                repeat(digitCount) { index ->
                    val digit = value.getOrNull(index)
                    val activeIndex = value.length.coerceAtMost(digitCount - 1)
                    val isActive = focused && index == activeIndex

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(digitHeight)
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(backgroundColor)
                            .border(
                                width = if (isActive) 1.5.dp else 1.dp,
                                color = if (isActive) primaryColor else borderColor,
                                shape = RoundedCornerShape(cornerRadius),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            digit != null -> {
                                Text(
                                    text = digit.toString(),
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor,
                                    ),
                                )
                            }

                            isActive && cursorOn -> {
                                Box(
                                    modifier = Modifier
                                        .width(1.5.dp)
                                        .height(20.dp)
                                        .background(primaryColor),
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}
