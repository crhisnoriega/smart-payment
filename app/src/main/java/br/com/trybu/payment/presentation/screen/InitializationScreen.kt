package br.com.trybu.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.R
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel

@Composable
fun InitializationScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit
) {
    Surface {

        LaunchedEffect(Unit) {
            viewModel.retrieveKey()
        }

        if (viewModel.state.wasInitialized) {
            viewModel.initialized()
            navigate.invoke("")
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("indicador_carregamento")
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 60.dp),
                painter = painterResource(id = R.drawable.logo_elosgate),
                contentDescription = ""
            )
        }
    }
}

