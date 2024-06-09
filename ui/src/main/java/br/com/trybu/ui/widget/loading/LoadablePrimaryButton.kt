package br.com.trybu.ui.widget.loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.com.trybu.ui.theme.blue_500
import br.com.trybu.ui.widget.ButtonProgress
import br.com.trybu.ui.widget.button.PrimaryButton


@Composable
fun LoadablePrimaryButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color? = blue_500,
    content: @Composable () -> Unit,
) {
    PrimaryButton(
        modifier = modifier,
        enabled = enabled || isLoading,
        containerColor = containerColor,
        onClick = onClick.takeIf { !isLoading } ?: {}
    ) {
        if (isLoading) {
            ButtonProgress()
        } else {
            content()
        }
    }
}