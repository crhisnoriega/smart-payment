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
            InformationScreen(viewModel = paymentViewModel) { route ->
                controller.navigate(route)
            }
        }

        composable(route = Routes.payment.operations) { entry ->
            val query = entry.arguments?.getString("query") ?: ""
            OperationsListingScreen(viewModel = paymentViewModel, query = query) { route ->
                if (route == "back") controller.popBackStack() else controller.navigate(route)
            }
        }

        composable(route = Routes.payment.details) { entry ->
            val jsonRaw = entry.arguments?.getString("operation")
            val operation =
                Gson().fromJson(jsonRaw, RetrieveOperationsResponse.Operation.TransactionType::class.java)
            DetailsScreen(viewModel = hiltViewModel(), operation = operation) {
                controller.popBackStack()
            }
        }
    }
}