package com.wenbo.test.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wenbo.test.ui.screen.CurrencyManagementScreen
import com.wenbo.test.ui.screen.MainScreen
import com.wenbo.test.viewmodel.WalletViewModel


@Composable
fun WalletNavHost(navController: NavHostController) {
    val sharedViewModel = hiltViewModel<WalletViewModel>()
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                sharedViewModel,
                onNavigateToCurrencyManagement = {
                    navController.navigate("currency_management")
                }
            )
        }
        composable("currency_management") {
            CurrencyManagementScreen(sharedViewModel,onBack = { navController.popBackStack() })
        }
    }
}