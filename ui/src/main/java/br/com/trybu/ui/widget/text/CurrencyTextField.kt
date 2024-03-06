package br.com.trybu.ui.widget.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.trybu.ui.theme.Annotation1
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.gray_700
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

const val CENTS_TO_REALS_POINTS = 2

fun Int.centsToReals(): BigDecimal = BigDecimal(this).movePointLeft(CENTS_TO_REALS_POINTS)
fun BigDecimal.realsToCents(): Int = movePointRight(CENTS_TO_REALS_POINTS).intValueExact()
fun Double.realsToCents(): Int = BigDecimal(this).realsToCents()

val localeBR = Locale("pt", "BR")
val blrFormatter = NumberFormat.getCurrencyInstance(localeBR).apply { }
val currencyFormatter = NumberFormat.getNumberInstance(localeBR).apply {
    minimumFractionDigits = 2
}

fun Int.formatBlr() = blrFormatter.format(centsToReals())
fun BigDecimal.formatBlr() = blrFormatter.format(this)
fun BigDecimal.formatCurrency() = currencyFormatter.format(this)

@Composable
fun CurrencyTextField(
    valueInCents: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = { Text("R\$", style = Annotation1.copy(color = gray_700)) },
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(8.dp),
    colors: TextFieldColors = appTextFieldColors(),
) {
    val formatted by remember(valueInCents) {
        derivedStateOf { valueInCents.centsToReals().formatCurrency() }
    }
    AppTextField(
        value = TextFieldValue(text = formatted, selection = TextRange(formatted.length)),
        onValueChange = { value ->
            val rawValueInCents = value.text.filter { it.isDigit() }
            val intValueInCents = runCatching { rawValueInCents.toInt() }.getOrDefault(0)
            onValueChange(intValueInCents)
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        placeholder = placeholder
    )
}

class HideZeroVisualTransform : VisualTransformation {

    private val zeroFormatted = 0.centsToReals().formatCurrency()

    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = if (text.text == zeroFormatted) "" else text.text
        return TransformedText(
            text = AnnotatedString(transformed),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return if (transformed.isEmpty()) 0 else offset
                }
                
                override fun transformedToOriginal(offset: Int): Int {
                    return offset
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewCurrencyTextField() {
    AppTheme {
        Surface {
            Column {
                CurrencyTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    valueInCents = 130010,
                    onValueChange = {}
                )
                val hideZero = remember { HideZeroVisualTransform() }
                CurrencyTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    valueInCents = 0,
                    onValueChange = {},
                    visualTransformation = hideZero
                )
                CurrencyTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    valueInCents = 5,
                    onValueChange = {},
                    visualTransformation = hideZero
                )
            }
        }
    }
}



