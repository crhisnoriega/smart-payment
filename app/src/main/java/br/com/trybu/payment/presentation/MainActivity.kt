package br.com.trybu.payment.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import br.com.trybu.payment.navigation.MainNavigation
import br.com.trybu.payment.presentation.viewmodel.MainViewModel
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.ui.theme.AppTheme
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppTheme {
                MainNavigation(
                    controller = navController,
                    mainViewModel = viewModel
                )
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.qrCode.observe(this) {
                when (it) {
                    is br.com.trybu.payment.presentation.viewmodel.QRUIState.ReadQRCode -> {
                        val scanner = IntentIntegrator(this)
                        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        scanner.setBeepEnabled(false)
                        scanner.setPrompt("Scanning Code");
                        scanner.initiateScan()
                    }

                    else -> {}
                }
            }
        }, 500)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result.contents == null) {
            } else {
                viewModel.qrCode(result.contents)
            }
        }
    }
}

