package br.com.trybu.payment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.trybu.payment.presentation.ui.InformationScreen
import br.com.trybu.payment.presentation.ui.OperationsListingScreen
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel

@Composable
fun MainNavigation(
    controller: NavHostController,
    paymentViewModel: PaymentViewModel
) {
    NavHost(
        navController = controller,
        route = Routes.main,
        startDestination = Routes.payment.infos
    ) {
        composable(route = Routes.payment.infos) {
            InformationScreen(viewModel = paymentViewModel) {
                controller.navigate("operations")
            }
        }

        composable(route = Routes.payment.operations) {
            OperationsListingScreen(viewModel = paymentViewModel)
        }
    }
}