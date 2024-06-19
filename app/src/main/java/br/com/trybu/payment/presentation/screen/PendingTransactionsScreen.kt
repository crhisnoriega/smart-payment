package br.com.trybu.payment.presentation.screen

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.TableInfo
import br.com.trybu.payment.R
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.db.entity.Transaction
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.payment.presentation.viewmodel.PendingViewModel
import br.com.trybu.ui.theme.Subtitle1
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.AppTopBar
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.card.AppCard

@Composable
fun PendingTransactionsScreen(
    operationInfoViewModel: PendingViewModel = hiltViewModel(),
    goToInput: () -> Unit,
    onBackPress: () -> Unit
) {
    val pendingTransactions = operationInfoViewModel.pendingTransactions
    val state = operationInfoViewModel.statePending

    LaunchedEffect(Unit) {
        operationInfoViewModel.checkPendingTransactions()
    }

    if (state == "success") goToInput()

    BackHandler { onBackPress() }

    AppScaffold(modifier = Modifier.fillMaxSize(), topBar = {
        AppTopBar(painter = painterResource(id = R.drawable.logo_elosgate)) {
            onBackPress()
        }
    }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Transações pendentes", style = Subtitle2)
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(pendingTransactions) { transaction ->
                    OperationPendingCard(operation = transaction) { }
                }
            }

            PrimaryButton(onClick = {
                operationInfoViewModel.checkPendingTransactions()
            }) {
                Text(text = "Tentar Envio")
            }
        }
    }

}


@Composable
fun OperationPendingCard(
    modifier: Modifier = Modifier,
    operation: Transaction,
    onSelect: (RetrieveOperationsResponse.Operation.TransactionType) -> Unit
) {

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Rounded.ShoppingCart,
                tint = blue_500,
                contentDescription = ""
            )
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = operation.id, style = Subtitle2
                )
                Text(
                    text = operation.createDate, style = Subtitle2
                )
                Text(
                    text = operation.status.toString(), style = Subtitle2
                )
            }
        }
    }
}