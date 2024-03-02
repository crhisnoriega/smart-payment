package br.com.trybu.payment.presentation.screen

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.navigation.Routes
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.Body1
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.PrimaryTopBar
import br.com.trybu.ui.widget.button.PrimaryButton

@Composable
fun InformationScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    route: (String) -> Unit,
) {
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            PrimaryTopBar(
                title = {
                    Text(
                        modifier = Modifier.testTag("titulo_tela"),
                        text = "Pagamentos"
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            Column(
                modifier = Modifier
                    .weight(1f, false)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.padding(top = 60.dp))
                Text(text = "Numero de Serie", style = Title2, color = Color.Black)
                Text(text = Build.SERIAL, style = Body1)
            }

            PrimaryButton(
                onClick = { route(Routes.payment.operations) },
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.End)
            ) {
                Text(text = "Operações")
            }
        }
    }

}