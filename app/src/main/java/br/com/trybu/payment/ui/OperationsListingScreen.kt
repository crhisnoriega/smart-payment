package br.com.trybu.payment.ui

import android.text.SpannableStringBuilder
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.payment.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.Annotation1
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.orange_500
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.card.AppCard


@Composable
fun OperationsListingScreen(
    viewModel: PaymentViewModel
) {
    val state = viewModel.state

    if (state.error != null) {
        AlertDialog(
            text = { Text(text = state.error) },
            onDismissRequest = { },
            confirmButton = {
                PrimaryButton(onClick = { viewModel.dismissError() }) {
                    Text(text = "Confirmar")
                }
            }
        )
    }

    if (state.paymentState != null) {
        AlertDialog(
            shape = RectangleShape,
            text = { Text(text = state.paymentState) },
            onDismissRequest = { },
            confirmButton = {

            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.operations) {
            OperationCard(operation = it) { operation ->
                viewModel.doPayment(operation)
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                PrimaryButton(onClick = {
                    viewModel.retrieveKey()
                }) {
                    Text(text = "key")
                }
                PrimaryButton(onClick = {
                    viewModel.retrieveOperations("11111111111")
                }) {
                    Text(text = "ops")
                }
            }
        }


    }
}

@Composable
fun OperationCard(
    modifier: Modifier = Modifier,
    operation: RetrieveOperationsResponse.Items.Operation,
    onClick: (operation: RetrieveOperationsResponse.Items.Operation) -> Unit
) {

    val spannableString = SpannableStringBuilder(operation.htmlString).toString()
    val spanned = HtmlCompat.fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_COMPACT)


    AppCard(modifier.fillMaxWidth()) {
        Column(modifier = Modifier.clickable(onClick = {
            onClick.invoke(operation)
        })) {
            Row(
                Modifier
                    .padding(top = 32.dp, bottom = 8.dp)
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("tipo_chave_icone"),
                    imageVector = Icons.Rounded.ShoppingCart,
                    tint = orange_500,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier.testTag("tipo_chave_nome"),
                    text = spanned.toAnnotatedString(),
                    style = Subtitle2
                )
                Spacer(modifier = Modifier.weight(1f))

            }
            Text(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                    .testTag("chave"), text = operation.transactionId ?: "", style = Annotation1
            )
        }
    }
}

