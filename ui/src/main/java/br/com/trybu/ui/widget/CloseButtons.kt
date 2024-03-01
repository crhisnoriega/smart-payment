package br.com.trybu.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.gray_700
import br.com.trybu.payment.ui.R


@Composable
fun CloseButton(modifier: Modifier = Modifier, onClose: () -> Unit) {
    IconButton(
        modifier = modifier,
        onClick = onClose
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            tint = gray_700,
            painter = painterResource(id = R.drawable.ic_clear_default),
            contentDescription = stringResource(R.string.action_close)
        )
    }
}

@Composable
fun OutlinedCloseButton(modifier: Modifier = Modifier, onClose: () -> Unit) {
    IconButton(
        modifier = modifier,
        onClick = onClose
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            tint = gray_700,
            painter = painterResource(id = R.drawable.ic_clear_outline),
            contentDescription = stringResource(R.string.action_close)
        )
    }
}

@Preview
@Composable
fun PreviewCloseButtons() {
    AppTheme {
        Surface() {
            Row {
                CloseButton {}
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedCloseButton {}
            }
        }
    }
}