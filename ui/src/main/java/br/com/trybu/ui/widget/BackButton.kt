package br.com.trybu.ui.widget

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.payment.ui.R
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.theme.blue_600

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(id = R.string.action_back),
            tint = blue_500
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBackButton() {
    AppTheme {
        Surface(modifier = Modifier) {
            BackButton(
                modifier = Modifier,
                onClick = {}
            )
        }
    }
}