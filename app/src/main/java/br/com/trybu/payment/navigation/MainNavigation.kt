package br.com.trybu.payment.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.presentation.screen.DetailsScreen
import br.com.trybu.payment.presentation.screen.InformationScreen
import br.com.trybu.payment.presentation.screen.InitializationScreen
import br.com.trybu.payment.presentation.screen.OperationsListingScreen
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import com.google.gson.Gson

@Composable
fun MainNavigation(
    controller: NavHostController,
    paymentViewModel: OperationInfoViewModel
) {
    NavHost(
        navController = controller,
        route = Routes.main,
        startDestination = Routes.payment.initialize
    ) {
        composable(route = Routes.payment.initialize) {
            InitializationScreen(viewModel = paymentViewModel) {
                controller.navigate(Routes.payment.information)
            }
        }

        composable(route = Routes.payment.information) {
            InformationScreen(viewModel = paymentViewModel) {
                controller.navigate(Routes.payment.operations)
            }
        }

        composable(route = Routes.payment.operations) {
            OperationsListingScreen(viewModel = paymentViewModel) {
                controller.navigate(it)
            }
        }

        composable(route = Routes.payment.details) { entry ->
            val jsonRaw = entry.arguments?.getString("operation")
            val operation =
                Gson().fromJson(jsonRaw, RetrieveOperationsResponse.Operation::class.java)
            DetailsScreen(viewModel = hiltViewModel(), operation = operation)
        }
    }
}