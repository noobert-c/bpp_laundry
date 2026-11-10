package com.example.bpp_laundry.navigation

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bpp_laundry.DashboardScreen
import com.example.bpp_laundry.ForgotPasswordscreen
import com.example.bpp_laundry.NewPasswordScreen
import com.example.bpp_laundry.OrderListScreen
import com.example.bpp_laundry.OtpSuccessScreen
import com.example.bpp_laundry.OtpVerificationScreen
import com.example.bpp_laundry.PasswordSuccessScreen
import com.example.bpp_laundry.SplashScreen

import com.example.bpp_laundry.onboarding.*
import com.example.bpp_laundry.ui.preview.LoginScreen
import com.example.bpp_laundry.ui.preview.RegisterScreen
import kotlinx.coroutines.delay


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen
    ) {
        composable(Screen.SplashScreen) {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(1500L)

                navController.navigate(Screen.OnBoarding1) {
                    popUpTo(Screen.SplashScreen) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
        composable(Screen.OnBoarding1) {
            OnBoarding_1(
                onSkip = {
                    navController.navigate(Screen.Login)
                },
                onNext = {
                    navController.navigate(Screen.OnBoarding2)
                }
            )
        }



        composable(Screen.OnBoarding2) {
            OnBoarding_2(
                onSkip = {
                    navController.navigate(Screen.Login)
                },
                onNext = {
                    navController.navigate(Screen.OnBoarding3)
                }
            )
        }

        composable(Screen.OnBoarding3) {
            OnBoarding_3(
                onNext = {
                    navController.navigate(Screen.Login)
                }
            )
        }

        composable(Screen.Login) {
            LoginScreen(
                onLanjutkan = {
                    navController.navigate(Screen.Home)
                },
                onLupaSandi = {
                    navController.navigate(Screen.ForgotPassword)
                },
                onDaftar = {
                    navController.navigate(Screen.Register)
                }
            )
        }

        composable(Screen.Register) {
            RegisterScreen(
                onDaftar = {
                    navController.navigate(Screen.Login)
                },
                onMasuk = {
                    navController.navigate(Screen.Login)
                }
            )
        }

        composable(Screen.ForgotPassword) {
            ForgotPasswordscreen(
                onPrevious = {
                    navController.navigate(Screen.Login)
                },
                onKirimTautan = {
                    navController.navigate(Screen.OtpVerification)
                },
                onBackToLogin = {
                    navController.navigate(Screen.Login)
                }
            )
        }

        composable(Screen.OtpVerification) {
            OtpVerificationScreen()
        }

        composable(Screen.OtpSuccess) {
            OtpSuccessScreen()
        }

        composable(Screen.NewPasswordI) {
            NewPasswordScreen()
        }

        composable(Screen.NewPasswordSuccess) {
            PasswordSuccessScreen()
        }

        composable(Screen.Home) {
            DashboardScreen()
        }

        composable(Screen.Orders) {
            OrderListScreen()
        }
    }
}