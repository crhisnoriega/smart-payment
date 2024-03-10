package br.com.trybu.payment.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import br.com.trybu.payment.navigation.MainNavigation
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.payment.presentation.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.AppTheme
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: OperationInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppTheme {
                MainNavigation(
                    controller = navController, paymentViewModel = viewModel
                )
            }
        }

        viewModel.uiState.observe(this) {
            if (it == "qrcode") {
                val scanner = IntentIntegrator(this)
                scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                scanner.setBeepEnabled(false)
                scanner.setPrompt("Scanning Code");
                scanner.initiateScan()
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                    .show()
                viewModel.qrCode(result.contents)
            }
        }
    }
}

