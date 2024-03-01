package br.com.trybu.ui.widget.tutorial

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PageIndicators(modifier: Modifier = Modifier, itemCount: Int, currentIndex: Int) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = spacedBy(16.dp)
    ) {
        items(itemCount) { index ->
            PageDot(isFocused = index == currentIndex)
        }
    }
}

@Composable
fun PageDot(isFocused: Boolean) {
    Surface(
        modifier = Modifier.size(8.dp),
        shape = CircleShape,
        color = if (isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        content = {}
    )
}