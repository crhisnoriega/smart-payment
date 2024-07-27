package br.com.trybu.payment.presentation.screen

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.navigation.Routes
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.payment.presentation.viewmodel.UIState
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.Body1
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.widget.AppBottomSheet
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.loading.LoadablePrimaryButton
import br.com.trybu.ui.widget.text.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(
    viewModel: OperationInfoViewModel = hiltViewModel(),
    route: (String) -> Unit,
) {

    val bottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uiState = viewModel._uiState
    val qrCode by viewModel.qrCode.observeAsState()
    var reprint by remember { mutableStateOf(true) }

    LaunchedEffect(qrCode) {
        if (!qrCode.isNullOrEmpty()) {
            val goTo = Routes.payment.operations.replace("{query}", Uri.encode(qrCode))
            route(goTo)
        }
    }

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

        }
    ) { padding ->
        InformationContent(
            goToQRCode = { viewModel.openCamera() },
            goToOps = {
                val goTo = Routes.payment.operations.replace("{query}", it)
                route(goTo)
            },
            printLast = {
                viewModel.printLast()
            },
            reprint = reprint
        )

        when (uiState) {
            is UIState.InitializeSuccess -> {
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
                        Text(text = uiState.establishmentDocument ?: "", style = Body1)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Razão Social",
                            style = Title2.copy(fontSize = 18.sp),
                            color = Color.Black
                        )
                        Text(text = uiState.establishmentName ?: "", style = Body1)
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun InformationContent(
    goToOps: (String) -> Unit,
    goToQRCode: () -> Unit,
    printLast: () -> Unit,
    reprint: Boolean
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
            Text(
                text = "Digite o CPF ou número de contrato para localizar a venda",
                style = Title2.copy(fontSize = 18.sp),
                color = Color.Black
            )
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

        Column(modifier = Modifier.align(alignment = Alignment.End)) {

            LoadablePrimaryButton(
                isLoading = reprint,
                onClick = { printLast() },
                modifier = Modifier
                    .padding(horizontal = 32.dp)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Print,
                        contentDescription = "",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = "Imprimir último\ncomprovante",
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }

            }

            PrimaryButton(
                onClick = { goToQRCode() },
                modifier = Modifier
                    .padding(horizontal = 32.dp)

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
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    AppTheme {

    }
}

fun String.digits() = filter { it.isDigit() }