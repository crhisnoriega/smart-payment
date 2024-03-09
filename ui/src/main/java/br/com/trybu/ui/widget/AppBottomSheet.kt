package br.com.trybu.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.Body2
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.widget.button.PrimaryButton
import br.com.trybu.ui.widget.icon.PrimaryIconContainer
import br.com.trybu.payment.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    painter: Painter,
    title: String,
    message: String,
    actionLabel: String,
    onClickAction: () -> Unit,
    onDismiss: () -> Unit,
    state: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismiss,
        dragHandle = {},
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        AppBottomSheetContent(
            painter = painter,
            title = title,
            message = message,
            actionLabel = actionLabel,
            onClickAction = onClickAction,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    painter: Painter,
    title: String,
    onDismiss: () -> Unit,
    state: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismiss,
        dragHandle = {},
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        AppBottomSheetContent(
            painter = painter,
            title = title,
            onDismiss = onDismiss,
            content = content
        )
    }
}

@Composable
fun AppBottomSheetContent(
    painter: Painter,
    title: String,
    message: String,
    actionLabel: String,
    onClickAction: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            CloseButton(onClose = onDismiss)
        }
        PrimaryIconContainer(
            modifier = Modifier.padding(horizontal = 24.dp),
            painter = painter
        )
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp),
            style = Subtitle2,
            text = title
        )
        Text(
            modifier = Modifier.padding(all = 24.dp),
            style = Body2,
            text = message
        )
        HorizontalDivider()
        PrimaryButton(
            modifier = Modifier.padding(all = 24.dp),
            onClick = onClickAction
        ) {
            Text(text = actionLabel)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AppBottomSheetContent(
    painter: Painter,
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            CloseButton(onClose = onDismiss)
        }
        PrimaryIconContainer(
            modifier = Modifier.padding(horizontal = 24.dp),
            painter = painter
        )
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp),
            style = Subtitle2,
            text = title
        )
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAppBottomSheet() {
    AppTheme {
        Surface(modifier = Modifier, color = Color.White) {
            AppBottomSheetContent(
                painter = painterResource(id = R.drawable.baseline_store_24),
                title = "Solicitação não concluída",
                message = "Falha na comunicação. Tente novamente",
                actionLabel = "Entendi",
                onClickAction = {},
                onDismiss = {},
            )
        }
    }
}
             