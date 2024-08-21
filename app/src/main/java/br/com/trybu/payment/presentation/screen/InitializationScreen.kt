package br.com.trybu.payment.presentation.screen

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.trybu.payment.R
import br.com.trybu.payment.navigation.Routes
import br.com.trybu.payment.presentation.viewmodel.InitializationnViewModel
import br.com.trybu.payment.presentation.viewmodel.UIEvent
import br.com.trybu.payment.presentation.viewmodel.UIState
import br.com.trybu.ui.theme.Title2
import br.com.trybu.ui.widget.AppBottomSheet
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitializationScreen(
    viewModel: InitializationnViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit
) {
    Surface {

        val bottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val uiState = viewModel._uiState
        val uiEvent by viewModel.uiEvent.collectAsState(initial = UIEvent.None)

        LaunchedEffect(Unit) {
            viewModel.retrieveKey()
        }

        LaunchedEffect(uiEvent) {
            when (uiEvent) {
                is UIEvent.GoToInformation -> navigate(Routes.payment.information.replace("{state}",
                    Uri.encode(Gson().toJson(viewModel._uiState))))
                is UIEvent.GoToPending -> navigate(Routes.payment.pending)
                else -> {}
            }
        }

        Log.i("log", "viewModel.state.showInfo: ${viewModel._uiState}")
        when (uiState) {
            is UIState.InitializeFail -> {
                AppBottomSheet(
                    painter = painterResource(id = br.com.trybu.payment.ui.R.drawable.baseline_store_24),
                    title = "",
                    onDismiss = {
                        Handler(Looper.getMainLooper()).postDelayed({
                            viewModel.exit()
                        }, 0)
                    },
                    state = bottomSheet
                ) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = viewModel._uiState.error ?: "",
                            style = Title2.copy(fontSize = 18.sp),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }

            else -> {}
        }
        Box(
            modifier = modifier.fillMaxSize()
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

