package br.com.trybu.payment.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.navigation.MainNavigation
import br.com.trybu.payment.util.toAnnotatedString
import br.com.trybu.payment.viewmodel.PaymentViewModel
import br.com.trybu.ui.theme.Annotation1
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.orange_500
import br.com.trybu.ui.widget.AppScaffold
import br.com.trybu.ui.widget.BackButton
import br.com.trybu.ui.widget.PrimaryTopBar
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.card.AppCard
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

