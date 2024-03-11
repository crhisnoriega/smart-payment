package br.com.trybu.payment.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import br.com.trybu.ui.theme.Body1

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
fun EmptyList(modifier: Modifier = Modifier) {
    Surface {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Nenhum registro encontrado",
                modifier = Modifier.align(Alignment.Center),
                style = Body1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFullscreenLoading() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        LoadingFullScreen(
            modifier = Modifier
        )
    }
}