package br.com.trybu.payment.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.trybu.payment.R
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.AppTopBar

@Composable
fun PendingTransactionsScreen() {
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(painter = painterResource(id = R.drawable.logo_elosgate)) {

            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 34.dp, end = 24.dp, top = 60.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

        }
    }
}