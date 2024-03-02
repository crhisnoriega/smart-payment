package br.com.trybu.payment.ui

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.Body1
import br.com.trybu.ui.theme.Title1
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.PrimaryTopBar
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.button.TertiaryButton

@Composable
fun InfosScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    route: (String) -> Unit,
) {
    AppScaffold(
        modifier = Modifier,
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
                .fillMaxWidth()
        )
        {
            Spacer(modifier = Modifier.padding(top = 60.dp))
            Text(text = "SN", style = Title2, color = Color.Black)
            Text(text = Build.SERIAL, style = Body1)
            PrimaryButton(
                onClick = { route("operations") },
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(text = "Operações")
            }
        }
    }

}