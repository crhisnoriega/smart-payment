package br.com.trybu.ui.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ButtonProgress(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary
) {
    CircularProgressIndicator(
        modifier = modifier.size(18.dp),
        strokeWidth = 2.dp,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewButtonProgress() {
    Surface(modifier = Modifier, color = Color.Black) {
        ButtonProgress(
            modifier = Modifier,
            color = Color.White
        )
    }
}