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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.trybu.payment.R
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.button.PrimaryButton

@Composable
fun DetailsScreen(
    viewModel: PaymentViewModel,
    operation: RetrieveOperationsResponse.Operation
) {
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = blue_500)
            ) {
                Image(
                    modifier = Modifier.padding(horizontal = 120.dp),
                    painter = painterResource(id = R.drawable.logo_elosgate),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize().padding(all = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = operation.htmlString.toAnnotatedString())
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(operation.transactionsTypes) {
                    PrimaryButton(onClick = { /*TODO*/ }) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(text = it.htmlString.toAnnotatedString())
                        }
                    }
                }
            }
        }
    }
}