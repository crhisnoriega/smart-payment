package br.com.trybu.ui.widget.icon

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.payment.ui.R

@Composable
fun PrimaryIconContainer(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(
        modifier = modifier.size(64.dp),
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(32.dp)
                .padding(16.dp),
            tint = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPrimaryIconContainer() {
    AppTheme {
        Surface(modifier = Modifier, color = Color.White) {
            PrimaryIconContainer(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.baseline_store_24)
            )
        }
    }
}