package br.com.trybu.payment.presentation.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.R
import br.com.trybu.payment.navigation.Routes
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.Body1
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.theme.blue_600
import br.com.trybu.ui.widget.AppBottomSheet
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.AppTopBar
import br.com.trybu.ui.widget.PrimaryTopBar
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.text.AppTextField
import br.com.trybu.ui.widget.text.mask
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(
    viewModel: OperationInfoViewModel = hiltViewModel(),
    route: (String) -> Unit,
) {

    val bottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state = viewModel.state
    val qrCode by viewModel.qrCode.observeAsState()


    LaunchedEffect(qrCode) {
        if (!qrCode.isNullOrEmpty()) {
            val goTo = Routes.payment.operations.replace("{query}", Uri.encode(qrCode!!))
            route(goTo)
        }
    }

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(painter = painterResource(id = R.drawable.logo_elosgate))
        }
    ) { padding ->
        InformationContent(
            goToQRCode = { viewModel.openCamera() },
            goToOps = {
                val goTo = Routes.payment.operations.replace("{query}", it)
                route(goTo)
            }
        )

        if (state.showInfo) {
            AppBottomSheet(
                painter = painterResource(id = br.com.trybu.payment.ui.R.drawable.baseline_store_24),
                title = "Dados do estabelecimento",
                onDismiss = { viewModel.hideInfo() },
                state = bottomSheet
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CNPJ",
                        style = Title2.copy(fontSize = 18.sp),
                        color = Color.Black
                    )
                    Text(text = state.establishmentDocument ?: "", style = Body1)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Razão Social",
                        style = Title2.copy(fontSize = 18.sp),
                        color = Color.Black
                    )
                    Text(text = state.establishmentName ?: "", style = Body1)
                    Spacer(modifier = Modifier.height(50.dp))
                }

            }

        }
    }
}

@Composable
fun InformationContent(
    goToOps: (String) -> Unit,
    goToQRCode: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    )
    {
        HorizontalDivider()
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.Start)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Digite CPF, número de contrato para localizar a venda", style = Title2.copy(fontSize = 18.sp), color = Color.Black)
            AppTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            )
            PrimaryButton(
                onClick = { goToOps(query) },
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.End)
            ) {
                Text(text = "Procurar venda")
            }
        }

        PrimaryButton(
            onClick = { goToQRCode() },
            modifier = Modifier
                .padding(32.dp)
                .align(alignment = Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Filled.AccountBox,
                contentDescription = "",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(text = "QRCode")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    AppTheme {

    }
}

fun String.digits() = filter { it.isDigit() }