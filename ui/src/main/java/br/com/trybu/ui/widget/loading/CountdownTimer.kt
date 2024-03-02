package br.com.trybu.ui.widget.loading

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.Subtitle1
import br.com.trybu.ui.theme.gray_100
import br.com.trybu.ui.theme.gray_900


const val MINUTE_IN_SECONDS = 60

@Composable
fun CountdownTimer(
    modifier: Modifier = Modifier,
    totalSeconds: Int,
    remainingSeconds: Int
) {
    val progress by remember(totalSeconds, remainingSeconds) {
        derivedStateOf { remainingSeconds.toFloat() / totalSeconds.toFloat() }
    }
    val time by remember(totalSeconds, remainingSeconds) {
        derivedStateOf {
            val minutes = remainingSeconds / MINUTE_IN_SECONDS
            val seconds = remainingSeconds - (minutes * MINUTE_IN_SECONDS)
            buildString {
                append(String.format("%1d", minutes))
                append(":")
                append(String.format("%1$02d", seconds))
            }
        }
    }
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing), label = ""
    )
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            modifier = Modifier
                .scale(scaleX = -1f, scaleY = 1f)
                .size(32.dp),
            progress = progressAnimation,
            color = Color.Black,
            trackColor = gray_100,
            strokeCap = StrokeCap.Round,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = time, style = Subtitle1.copy(color = gray_900))
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCountdownTimer() {
    AppTheme {
        Surface(modifier = Modifier) {
            CountdownTimer(
                modifier = Modifier.padding(24.dp),
                totalSeconds = 90,
                remainingSeconds = 59
            )
        }
    }
}