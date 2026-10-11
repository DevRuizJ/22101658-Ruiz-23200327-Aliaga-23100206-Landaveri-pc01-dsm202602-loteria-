package dev.ruizj.loteriaapp_dpa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ruizj.loteriaapp_dpa.model.AuthState
import dev.ruizj.loteriaapp_dpa.ui.screens.LiveDrawScreen
import dev.ruizj.loteriaapp_dpa.ui.screens.LoginScreen
import dev.ruizj.loteriaapp_dpa.ui.screens.RegisterScreen
import dev.ruizj.loteriaapp_dpa.ui.screens.TicketRegistrationScreen
import dev.ruizj.loteriaapp_dpa.viewmodel.AuthViewModel
import dev.ruizj.loteriaapp_dpa.viewmodel.LotteryViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object TicketRegistration : Screen("ticket_registration")
    object LiveDraw : Screen("live_draw")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    lotteryViewModel: LotteryViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    val startDestination = if (authState is AuthState.Authenticated) {
        Screen.TicketRegistration.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    authViewModel.clearError()
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.TicketRegistration.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    authViewModel.clearError()
                    navController.navigate(Screen.Login.route)
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.TicketRegistration.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TicketRegistration.route) {
            val userEmail = (authState as? AuthState.Authenticated)?.email ?: ""
            val userId = (authState as? AuthState.Authenticated)?.userId ?: ""

            TicketRegistrationScreen(
                userEmail = userEmail,
                userId = userId,
                authViewModel = authViewModel,
                lotteryViewModel = lotteryViewModel,
                onNavigateToLiveDraw = {
                    navController.navigate(Screen.LiveDraw.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.TicketRegistration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LiveDraw.route) {
            val userEmail = (authState as? AuthState.Authenticated)?.email ?: ""
            val userId = (authState as? AuthState.Authenticated)?.userId ?: ""

            LiveDrawScreen(
                userEmail = userEmail,
                userId = userId,
                lotteryViewModel = lotteryViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
