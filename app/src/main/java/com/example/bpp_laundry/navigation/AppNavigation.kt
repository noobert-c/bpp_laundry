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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bpp_laundry.auth.ForgotPasswordViewModel
import kotlinx.coroutines.delay


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login
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
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) {
                            inclusive = true
                        }
                    }
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
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Register) {
                            inclusive = true
                        }
                    }
                },
                onMasuk = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Register) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword) {
            ForgotPasswordscreen(
                onPrevious = {
                    navController.popBackStack()
                },
                onKirimTautan = {
                    navController.navigate(Screen.OtpVerification)
                },
                onBackToLogin = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                viewModel = forgotPasswordViewModel
            )
        }

        composable(Screen.OtpVerification) {
            OtpVerificationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onVerifyClick = {
                    navController.navigate(Screen.OtpSuccess)
                },
                onBackToLoginClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                viewModel = forgotPasswordViewModel
            )
        }

        composable(Screen.OtpSuccess) {
            OtpSuccessScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueClick = {
                    navController.navigate(Screen.NewPasswordI)
                },
                onBackToLoginClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.NewPasswordI) {
            NewPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    navController.navigate(Screen.NewPasswordSuccess)
                },
                onBackToLoginClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                viewModel = forgotPasswordViewModel
            )
        }

        composable(Screen.NewPasswordSuccess) {
            PasswordSuccessScreen(
                onContinueClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onBackToLoginClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home) {
            DashboardScreen()
        }

        composable(Screen.Orders) {
            OrderListScreen()
        }
    }
}