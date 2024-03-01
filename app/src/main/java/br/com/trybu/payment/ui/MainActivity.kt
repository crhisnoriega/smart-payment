package br.com.trybu.payment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.button.PrimaryButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppScaffold(
                    modifier = Modifier,
                    topBar = {

                    }

                ) {
                    TransactionListing()
                }
            }
        }
    }
}

@Composable
fun TransactionListing(
    viewModel: PaymentViewModel = hiltViewModel()
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            PrimaryButton(onClick = {
                viewModel.retrieveKey()
            }) {
                Text(text = "OK")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        TransactionListing()
    }
}