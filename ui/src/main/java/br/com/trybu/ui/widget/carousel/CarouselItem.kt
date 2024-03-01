package br.com.trybu.ui.widget.carousel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.Annotation1
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.widget.icon.SecondaryIconContainer
import br.com.trybu.payment.ui.R

@Composable
fun CarouselItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    text: String,
) {
    Surface(shape = RoundedCornerShape(8.dp)) {
        Column(
            modifier = modifier
                .width(80.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SecondaryIconContainer(painter = painter)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, style = Annotation1, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCarouselItem() {
    AppTheme {
        Surface(modifier = Modifier, color = Color.Black) {
            Row(modifier = Modifier.padding(16.dp)) {
                CarouselItem(
                    painter = painterResource(id = R.drawable.baseline_store_24),
                    text = "Item Text"
                )
                Spacer(modifier = Modifier.width(16.dp))
                CarouselItem(
                    painter = painterResource(id = R.drawable.baseline_store_24),
                    text = "Item Text Lines"
                )
            }
        }
    }
}