package br.com.trybu.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.trybu.payment.R
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.payment.util.toPaymentType
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.AppTopBar
import br.com.trybu.ui.widget.button.TertiaryButton
import br.com.trybu.ui.widget.loading.LoadablePrimaryButton

@Composable
fun DetailsScreen(
    viewModel: PaymentViewModel,
    operation: RetrieveOperationsResponse.Operation,
    goBack: () -> Unit
) {

    val state = viewModel.state
    val uiState by viewModel.uiState.observeAsState()

    LaunchedEffect(uiState) {
        if (uiState == "goback") goBack()
    }

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(painter = painterResource(id = R.drawable.logo_elosgate)) {
                goBack()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 60.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = operation.htmlString.toAnnotatedString())

                Column {
                    operation.transactionsTypes.forEach {
                        Text(text = it.htmlString.toAnnotatedString(), style = Subtitle2)
                    }
                }
            }

            Text(
                text = state.paymentState ?: "",
                textAlign = TextAlign.Center,
                style = Title2,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(bottom = 50.dp)
            )



            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    TertiaryButton(
                        onClick = { viewModel.abort() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Cancelar")
                    }
                }
                items(operation.transactionsTypes) {
                    LoadablePrimaryButton(
                        isLoading = state.currentTransactionId != null,
                        onClick = {
                            viewModel.doPayment(it)
                        }) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "${it.paymentType.toPaymentType()} R$ ${it.value}"
                            )
                        }
                    }
                }
            }
        }
    }
}