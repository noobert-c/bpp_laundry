
package com.example.bpp_laundry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import com.example.bpp_laundry.ui.components.AppOtpInput
import com.example.bpp_laundry.ui.layout.AppLayout
import com.example.bpp_laundry.ui.theme.AppColors
import kotlinx.coroutines.delay

private object OtpColors {
    val Primary = Color(0xFF0F7B6C)
    val OnPrimary = Color.White
    val Surface = Color.White
    val Border = Color(0xFFDDE7E6)
    val Muted = Color(0xFF6B7280)
    val Text = Color(0xFF111827)
}

private val Inter: FontFamily = FontFamily.Default
private val ButtonShape = RoundedCornerShape(14.dp)

private const val OTP_LENGTH = 6
private const val RESEND_SECONDS = 30

@Composable
fun OtpVerificationScreen(
    maskedEmail: String = "r***@freshlaundry.id",
    onBackClick: () -> Unit = {},
    onVerifyClick: (otp: String) -> Unit = {},
    onResendClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {},
) {
    var otp by rememberSaveable { mutableStateOf("") }
    var secondsLeft by rememberSaveable {
        mutableIntStateOf(RESEND_SECONDS)
    }
    var timerKey by remember { mutableIntStateOf(0) }

    val focusManager = LocalFocusManager.current
    val otpFocusRequester = remember { FocusRequester() }
    val isComplete = otp.length == OTP_LENGTH

    val submit = {
        if (isComplete) {
            focusManager.clearFocus()
            onVerifyClick(otp)
        }
    }

    LaunchedEffect(timerKey) {
        while (secondsLeft > 0) {
            delay(1_000)
            secondsLeft--
        }
    }

    LaunchedEffect(Unit) {
        otpFocusRequester.requestFocus()
    }

    AppLayout(
        horizontalPadding = 16.dp,
        verticalPadding = 8.dp,
        contentGap = 24.dp,

        topBar = {
            BackButton(onClick = onBackClick)
        },

        content = {
            // Heading
            Heading(maskedEmail = maskedEmail)

            // Input OTP
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Kode OTP",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = OtpColors.Muted,
                    ),
                )

                AppOtpInput(
                    value = otp,
                    onValueChange = { otp = it },
                    focusRequester = otpFocusRequester,
                    onDone = {
                        if (isComplete) {
                            submit()
                        } else {
                            focusManager.clearFocus()
                        }
                    },
                    primaryColor = OtpColors.Primary,
                    textColor = OtpColors.Text,
                    backgroundColor = OtpColors.Surface,
                    borderColor = OtpColors.Border,
                    digitCount = OTP_LENGTH,
                )
            }

            // Tombol verifikasi dan kirim ulang
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                VerifyButton(
                    text = "Verifikasi",
                    enabled = isComplete,
                    onClick = submit,
                )

                ResendBlock(
                    secondsLeft = secondsLeft,
                    onResendClick = {
                        otp = ""
                        secondsLeft = RESEND_SECONDS
                        timerKey++
                        onResendClick()
                    },
                )
            }
        },

        bottomBar = {
            Text(
                text = "Kembali ke halaman Masuk",
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(
                        role = Role.Button,
                        onClick = onBackToLoginClick,
                    )
                    .padding(
                        horizontal = 4.dp,
                        vertical = 6.dp,
                    ),
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = OtpColors.Primary,
                ),
            )
        },
    )
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(OtpColors.Surface)
            .border(
                width = 1.dp,
                color = OtpColors.Border,
                shape = CircleShape,
            )
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.ChevronLeft,
            contentDescription = "Kembali",
            tint = OtpColors.Text,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun Heading(
    maskedEmail: String,
) {
    val instructions = buildAnnotatedString {
        append(
            "Masukkan 6 digit kode yang dikirim ke email terdaftar Anda, "
        )

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = OtpColors.Text,
            )
        ) {
            append(maskedEmail)
        }

        append(".")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Verifikasi kode OTP",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                color = OtpColors.Text,
            ),
        )

        Text(
            text = instructions,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = OtpColors.Muted,
            ),
        )
    }
}

@Composable
private fun VerifyButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = OtpColors.Primary.copy(
                    alpha = if (enabled) 1f else 0.5f
                ),
                shape = ButtonShape,
            )
            .clip(ButtonShape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = OtpColors.OnPrimary,
            ),
        )
    }
}

@Composable
private fun ResendBlock(
    secondsLeft: Int,
    onResendClick: () -> Unit,
) {
    val canResend = secondsLeft <= 0
    val countdown = "%02d:%02d".format(
        secondsLeft / 60,
        secondsLeft % 60,
    )

    val resendText = buildAnnotatedString {
        append("Belum menerima kode? ")

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = if (canResend) {
                    OtpColors.Primary
                } else {
                    OtpColors.Primary.copy(alpha = 0.5f)
                },
            )
        ) {
            append("Kirim ulang kode")
        }

        if (!canResend) {
            append(" dalam $countdown")
        }
    }

    Text(
        text = resendText,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (canResend) {
                    Modifier.clickable(onClick = onResendClick)
                } else {
                    Modifier
                }
            ),
        style = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = OtpColors.Muted,
            textAlign = TextAlign.Center,
        ),
    )
}
