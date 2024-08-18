package br.com.trybu.payment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.presentation.screen.DetailsScreen
import br.com.trybu.payment.presentation.screen.InformationScreen
import br.com.trybu.payment.presentation.screen.InitializationScreen
import br.com.trybu.payment.presentation.screen.OperationsListingScreen
import br.com.trybu.payment.presentation.screen.PendingTransactionsScreen
import br.com.trybu.payment.presentation.viewmodel.UIState
import com.google.gson.Gson

@Composable
fun MainNavigation(
    controller: NavHostController

) {
    NavHost(
        navController = controller,
        route = Routes.main,
        startDestination = Routes.payment.initialize
    ) {
        composable(route = Routes.payment.initialize) {
            InitializationScreen { route ->
                controller.navigate(route) {
                    popUpTo(0)
                }
            }
        }

        composable(route = Routes.payment.information) { entry ->
            val state = entry.arguments?.getString("state")
            val intializationState = Gson().fromJson(state, UIState.InitializeSuccess::class.java)
            InformationScreen(initializeSuccess = intializationState) { route ->
                controller.navigate(route)
            }
        }

        composable(route = Routes.payment.operations) { entry ->
            val query = entry.arguments?.getString("query") ?: ""
            OperationsListingScreen(query = query) { route ->
                if (route == "back") controller.popBackStack() else controller.navigate(route)
            }
        }

        composable(route = Routes.payment.pending) {
            PendingTransactionsScreen(
                goToInput = {
                    controller.navigate(Routes.payment.information) {
                        popUpTo(0)
                    }
                },
                onBackPress = {
                    controller.navigate(Routes.payment.initialize)
                })
        }

        composable(route = Routes.payment.details) { entry ->
            val jsonRaw = entry.arguments?.getString("operation")
            val isRefund = entry.arguments?.getString("isRefund")
            val sessionID = entry.arguments?.getString("sessionID")

            val transactionType =
                Gson().fromJson(
                    jsonRaw,
                    RetrieveOperationsResponse.Operation.TransactionType::class.java
                )
            DetailsScreen(
                transactionType = transactionType,
                isRefund = isRefund.toBoolean(),
                sessionID = sessionID ?: "",
                goBack = {
                    controller.navigate(Routes.payment.information)
                },
                goInformation = {
                    controller.popBackStack()
                }
            )
        }
    }
}