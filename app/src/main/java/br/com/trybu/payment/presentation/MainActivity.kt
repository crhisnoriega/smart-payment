package br.com.trybu.payment.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import br.com.trybu.payment.navigation.MainNavigation
import br.com.trybu.payment.presentation.viewmodel.OperationInfoViewModel
import br.com.trybu.ui.theme.AppTheme
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: OperationInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppTheme {
                MainNavigation(
                    controller = navController
                )
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
            } else {
                //viewModel.qrCode(result.contents)
            }
        }
    }
}

