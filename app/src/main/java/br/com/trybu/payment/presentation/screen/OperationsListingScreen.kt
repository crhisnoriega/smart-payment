package br.com.trybu.payment.presentation.screen

import android.net.Uri
import android.text.SpannableStringBuilder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import br.com.trybu.payment.R
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.navigation.Routes
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.payment.util.toPaymentType
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.AppTopBar
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.card.AppCard
import br.com.trybu.ui.widget.loading.LoadablePrimaryButton
import com.google.gson.Gson

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
            state.operations == null -> EmptyList()
            state.operations != null && state.operations.isNotEmpty() == true -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp)
            ) {
                items(state.operations) {
                    OperationCard(operation = it) {
                        val routeStr = Routes.payment.details.replace(
                            "{operation}",
                            Uri.encode(Gson().toJson(it))
                        )
                        route(routeStr)
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        PrimaryButton(modifier = Modifier.padding(vertical = 16.dp), onClick = {
                            viewModel.retrieveOperations(query)
                        }) {
                            Text(text = "Atualizar")
                        }
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
                    .padding(all = 20.dp),
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 0.dp)
            ) {
                operation.transactionsTypes.forEach { transactionType ->
                    // Text(text = it.htmlString.toAnnotatedString(), style = Subtitle2)

                    LoadablePrimaryButton(
                        isLoading = false,
                        onClick = {
                            onSelect(transactionType)
                        }) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "${transactionType.paymentType.toPaymentType()} R$ ${transactionType.value}"
                            )
                        }
                    }
                }
            }
        }
    }
}

