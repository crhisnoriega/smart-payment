package br.com.trybu.payment.presentation.screen

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.payment.R
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.navigation.Routes
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.payment.util.toPaymentType
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.theme.danger_700
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.AppTopBar
import br.com.trybu.ui.widget.card.AppCard
import br.com.trybu.ui.widget.loading.LoadablePrimaryButton
import com.google.gson.Gson
import java.math.BigDecimal

@Composable
fun OperationsListingScreen(
    viewModel: OperationInfoViewModel,
    query: String,
    route: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.retrieveOperations(query)
    }

    val state = viewModel.state

    LaunchedEffect(state) {
        if (state.transactionType != null) {
            val routeStr = Routes.payment.details
                .replace(
                    "{operation}",
                    Uri.encode(Gson().toJson(state.transactionType))
                )
                .replace("{isRefund}", state.isRefund ?: "false")
            route(routeStr)

            state.transactionType = null
            state.isRefund = null
        }
    }



    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(painter = painterResource(id = R.drawable.logo_elosgate)) {
                route("back")
            }
        }
    ) {

        when {
            state.isLoading == true -> LoadingFullScreen()
            state.operations == null -> EmptyList(message = state.error ?: "")
            state.operations != null && state.operations.isNotEmpty() == true -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp)
            ) {
                items(state.operations) { item ->
                    OperationCard(operation = item) { selection ->
                        viewModel.tryGoToPayment(selection, item.isRefund.toString())
                    }
                }
            }

        }
    }
}

@Composable
fun OperationCard(
    modifier: Modifier = Modifier,
    operation: RetrieveOperationsResponse.Operation,
    onSelect: (RetrieveOperationsResponse.Operation.TransactionType) -> Unit
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Rounded.ShoppingCart,
                    tint = blue_500,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = operation.htmlString.toAnnotatedString(), style = Subtitle2
                )
            }

            if (operation.isRequestPayment == true) {
                Text(
                    text = "Pagamento de regularização",
                    style = Subtitle2.copy(color = danger_700),
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 2.dp)
            ) {
                operation.transactionsTypes.forEach { transactionType ->
                    LoadablePrimaryButton(
                        containerColor = if (operation.isRefund == true) danger_700 else blue_500,
                        isLoading = false,
                        onClick = {
                            onSelect(transactionType)
                        }) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "${if (operation.isRefund == true) "Estornar - " else ""}${transactionType.toPaymentType()} R$ ${transactionType.value}",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewOperationCard() {
    AppTheme {
        OperationCard(
            operation = RetrieveOperationsResponse.Operation(
                isRefund = true,
                isRequestPayment = true,
                htmlString = "<p style='text-align: center;'><b>Crédito<\\/b><br\\/><b>Valor: <\\/b> R\$ 30,98 <\\/p><p style='text-align: center;'><b>Crédito<\\/b><br\\/><b>Valor: <\\/b> R\$ 30,98 <\\/p>",
                transactionsTypes = listOf(
                    RetrieveOperationsResponse.Operation.TransactionType(
                        value = BigDecimal(100)
                    )
                )
            )
        ) {

        }

    }
}
