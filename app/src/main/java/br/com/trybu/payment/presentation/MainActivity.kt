package br.com.trybu.payment.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import br.com.trybu.payment.navigation.MainNavigation
import br.com.trybu.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppTheme {
                MainNavigation(
                    controller = navController, paymentViewModel = hiltViewModel()
                )
            }
        }
    }
}

