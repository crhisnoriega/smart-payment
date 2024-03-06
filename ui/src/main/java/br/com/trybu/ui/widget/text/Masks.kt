package br.com.trybu.ui.widget.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.VisualTransformation

private const val RAW_CPF_LENGTH = 11

@Composable
fun rememberCpfCnpjMask(value: String): VisualTransformation {
    return remember(value.length) {
        if (value.length <= RAW_CPF_LENGTH) {
            mask("###.###.###-##")
        } else {
            mask("##.###.###/####-##")
        }
    }
}