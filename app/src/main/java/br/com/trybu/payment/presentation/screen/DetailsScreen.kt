package br.com.trybu.payment.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.R
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.theme.danger_700
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.AppTopBar
import br.com.trybu.ui.widget.button.TertiaryButton

@Composable
fun DetailsScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    transactionType: RetrieveOperationsResponse.Operation.TransactionType,
    isRefund: Boolean,
    sessionID: String,
    goInformation: () -> Unit,
    goBack: () -> Unit
) {

    val state = viewModel.state
    val uiState by viewModel.uiState.observeAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            "goback" -> goBack()
            "goinformation" -> goInformation()
        }
    }

    LaunchedEffect(Unit) {
        if (isRefund) {
            viewModel.doRefund(transactionType.transactionId)
        } else {
            viewModel.doPayment(transactionType, sessionID)
        }
    }

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(painter = painterResource(id = R.drawable.logo_elosgate)) {
                goInformation()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 34.dp, end = 24.dp, top = 60.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                if (isRefund) {
                    Text(
                        text = "Estorno",
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = Title2.copy(color = danger_700)
                    )
                }
                Text(text = transactionType.htmlString.toAnnotatedString())
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
                        Text(text = "Cancelar Operação")
                    }
                }
            }
        }
    }
}