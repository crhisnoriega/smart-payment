package br.com.trybu.ui.widget.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.gray_0
import br.com.trybu.ui.theme.gray_100
import br.com.trybu.ui.theme.gray_1000

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    colors: CardColors = CardDefaults.outlinedCardColors(
        containerColor = gray_0,
        contentColor = gray_1000,
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier.clip(shape),
        colors = colors,
        shape = shape,
        border = BorderStroke(1.dp, gray_100),
        content = content
    )
}
