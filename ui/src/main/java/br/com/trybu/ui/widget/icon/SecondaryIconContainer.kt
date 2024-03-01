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
import br.com.trybu.payment.ui.R
import br.com.trybu.ui.theme.AppTheme

@Composable
fun SecondaryIconContainer(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
) {
    Surface(
        modifier = modifier.size(64.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(24.dp)
                .padding(horizontal = 20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSecondaryIconContainer() {
    AppTheme {
        Surface(modifier = Modifier, color = Color.White) {
            SecondaryIconContainer(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.baseline_store_24)
            )
        }
    }
}