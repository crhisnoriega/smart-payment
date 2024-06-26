package br.com.trybu.ui.widget.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.blue_500

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    containerColor: Color? = blue_500,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        enabled = enabled,
        contentPadding = contentPadding,
        content = content,
        colors = ButtonDefaults.buttonColors().copy(containerColor = containerColor ?: blue_500)
    )
}

@Preview
@Composable
fun PreviewPrimaryButton() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            PrimaryButton(
                modifier = Modifier.padding(16.dp),
                onClick = { },
                containerColor = blue_500
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBox,
                        contentDescription = "",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = "Imprimir última comprovante"
                    )
                }
            }
        }
    }
}