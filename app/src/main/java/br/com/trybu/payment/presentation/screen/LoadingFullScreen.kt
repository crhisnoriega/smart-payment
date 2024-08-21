package br.com.trybu.payment.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.trybu.ui.theme.Title2

@Composable
fun LoadingFullScreen(modifier: Modifier = Modifier) {
    Surface {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("indicador_carregamento")
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun EmptyList(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = message,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            style = Title2.copy(fontSize = 18.sp),
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFullscreenLoading() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        EmptyList(
            message = "Vazio"
        )
    }
}