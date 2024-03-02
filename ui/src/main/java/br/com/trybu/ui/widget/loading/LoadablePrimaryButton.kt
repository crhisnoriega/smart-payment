package br.com.trybu.ui.widget.loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.trybu.ui.widget.ButtonProgress
import br.com.trybu.ui.widget.button.PrimaryButton


@Composable
fun LoadablePrimaryButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    PrimaryButton(
        modifier = modifier,
        enabled = enabled || isLoading,
        onClick = onClick.takeIf { !isLoading } ?: {}
    ) {
        if (isLoading) {
            ButtonProgress()
        } else {
            content()
        }
    }
}