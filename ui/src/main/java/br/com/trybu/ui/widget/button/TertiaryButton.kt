package br.com.trybu.ui.widget.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.gray_1000
import br.com.trybu.ui.theme.gray_200
import br.com.trybu.ui.theme.gray_300
import br.com.trybu.ui.theme.gray_800

@Composable
fun TertiaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = tertiaryButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = BorderStroke(1.dp, gray_300),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        border = border,
        content = content,
        enabled = enabled,
        shape = shape,
        elevation = elevation,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    )
}

@Composable
fun tertiaryButtonColors() = ButtonDefaults.outlinedButtonColors(
    containerColor = Color.White,
    disabledContainerColor = gray_200,
    contentColor = gray_1000,
    disabledContentColor = gray_800
)
