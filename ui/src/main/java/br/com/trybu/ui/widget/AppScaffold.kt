package br.com.trybu.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.AppTheme

val ScaffoldInsets = WindowInsets(24.dp, 24.dp, 24.dp, 24.dp)
val ZeroInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)

@Composable
fun AppScaffold(
    title: String,
    modifier: Modifier = Modifier,
    navIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = ScaffoldInsets,
    content: @Composable (padding: PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            PrimaryTopBar(
                title = { Text(title) },
                navIcon = navIcon,
                actions = actions
            )
        },
        content = content,
        contentWindowInsets = windowInsets,
    )
}

@Composable
fun AppScaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = ScaffoldInsets,
    content: @Composable (padding: PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        content = content,
        contentWindowInsets = windowInsets,
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewAppScaffold() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppScaffold(
                modifier = Modifier,
                topBar = {
                    PrimaryTopBar(
                        modifier = Modifier,
                        navIcon = { BackButton {} },
                        title = { Text("Top App Bar") }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    Text(modifier = Modifier.align(Alignment.Center), text = "Scaffold Content")
                }
            }
        }
    }
}