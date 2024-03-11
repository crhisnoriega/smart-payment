package br.com.trybu.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.trybu.ui.theme.AppTheme
import br.com.trybu.ui.theme.Subtitle2
import br.com.trybu.ui.theme.blue_600


@Composable
fun AppTopBar(painter: Painter, back: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White)
    ) {

        back?.let {
            BackButton(modifier = Modifier.align(Alignment.CenterStart)) { it() }
        }
        Image(
            modifier = Modifier
                .padding(horizontal = 120.dp, vertical = 10.dp)
                .align(Alignment.Center),
            painter = painter,
            contentDescription = ""
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = navIcon,
        actions = actions,
        colors = topBarColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurfaceTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            ProvideTextStyle(
                value = Subtitle2, content = title
            )
        },
        navigationIcon = navIcon,
        actions = actions,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun topBarColors() = TopAppBarDefaults.centerAlignedTopAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
)

@Preview(showBackground = true)
@Composable
private fun PreviewTopBar() {
    AppTheme {
        Surface(modifier = Modifier, color = Color.White) {
            PrimaryTopBar(modifier = Modifier,
                navIcon = { BackButton {} },
                title = { Text("Top App Bar") })
        }
    }
}