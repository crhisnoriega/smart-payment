package br.com.trybu.payment.presentation.screen

import android.text.SpannableStringBuilder
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.ui.theme.Annotation1
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.button.TertiaryButton
import br.com.trybu.ui.widget.card.AppCard
import br.com.trybu.ui.widget.loading.LoadablePrimaryButton

@Composable
fun OperationsListingScreen(
    viewModel: PaymentViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.retrieveOperations("11111111111")
    }

    val state = viewModel.state

    if (state.error != null) {
        AlertDialog(text = { Text(text = state.error) }, onDismissRequest = { }, confirmButton = {
            PrimaryButton(onClick = { viewModel.dismissError() }) {
                Text(text = "Confirmar")
            }
        })
    }

    if (state.paymentState != null) {
        AlertDialog(shape = RectangleShape,
            text = { Text(text = state.paymentState) },
            onDismissRequest = { },
            confirmButton = {

            })
    }

    when {
        state.isLoading == true -> LoadingFullScreen()
        state.operations.isNotEmpty() -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.operations) {
                OperationCard(operation = it, viewModel = viewModel)
            }


            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PrimaryButton(modifier = Modifier.padding(vertical = 16.dp), onClick = {
                        viewModel.retrieveOperations("11111111111")
                    }) {
                        Text(text = "Atualizar")
                    }
                }
            }

        }
    }
}

@Composable
fun OperationCard(
    modifier: Modifier = Modifier,
    operation: RetrieveOperationsResponse.Items.Operation,
    viewModel: PaymentViewModel
) {

    val spannableString = SpannableStringBuilder(operation.htmlString).toString()
    val spanned = HtmlCompat.fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val state = viewModel.state


    val padding = if (operation.isHeader == true) {
        PaddingValues(top = 8.dp, start = 4.dp, end = 4.dp, bottom = 0.dp)
    } else {
        PaddingValues(horizontal = 4.dp)
    }
    AppCard(
        modifier
            .fillMaxWidth()
            .padding(padding)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .padding(top = 32.dp, bottom = 8.dp)
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (operation.isHeader == false) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Rounded.ShoppingCart,
                        tint = blue_500,
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Text(
                    text = spanned.toAnnotatedString(), style = Subtitle2
                )
            }
            if (operation.isHeader == false) {
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                ) {
                    TertiaryButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f, false)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(text = "Cancelar", color = blue_500)
                    }
                    LoadablePrimaryButton(
                        isLoading = state.currentTransactionId == operation.transactionId,
                        onClick = {
                            viewModel.doPayment(operation)
                        }, modifier = Modifier.weight(1f, false)
                    ) {
                        Text(text = "Pagar")
                    }

                }
            }
        }
    }
}

