package br.com.trybu.payment.presentation.screen

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.navigation.Routes
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.Body1
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.PrimaryTopBar
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.text.AppTextField

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
    ) { padding ->
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

                Text(
                    text = "Numero de Serie",
                    style = Title2.copy(fontSize = 18.sp),
                    color = Color.Black
                )
                Text(text = Build.SERIAL.replaceRange(0, 9, "*".repeat(9)), style = Body1)

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "CNPJ",
                    style = Title2.copy(fontSize = 18.sp),
                    color = Color.Black
                )
                Text(text = viewModel.state.establishmentDocument ?: "", style = Body1)



                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Razão Social",
                    style = Title2.copy(fontSize = 18.sp),
                    color = Color.Black
                )
                Text(text = viewModel.state.establishmentName ?: "", style = Body1)


                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Busca", style = Title2.copy(fontSize = 18.sp), color = Color.Black)
                AppTextField(
                    value = "111.111.111.-11",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp)
                )
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