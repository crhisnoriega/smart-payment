package br.com.trybu.ui.theme

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.trybu.payment.ui.R


val AppFont = FontFamily(
    Font(resId = R.font.manrope_extralight, weight = FontWeight.ExtraLight),
    Font(resId = R.font.manrope_light, weight = FontWeight.Light),
    Font(resId = R.font.manrope_regular, weight = FontWeight.Normal),
    Font(resId = R.font.manrope_medium, weight = FontWeight.Medium),
    Font(resId = R.font.manrope_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.manrope_bold, weight = FontWeight.Bold),
    Font(resId = R.font.manrope_extrabold, weight = FontWeight.ExtraBold),
    Font(resId = R.font.manrope_extrabold, weight = FontWeight.Black),
)

val Title1 = TextStyle(fontFamily = AppFont, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
val Title2 = TextStyle(fontFamily = AppFont, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

val Subtitle1 = TextStyle(fontFamily = AppFont, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
val Subtitle2 = TextStyle(fontFamily = AppFont, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)

val Body1 = TextStyle(fontFamily = AppFont, fontSize = 16.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.48.sp)
val Body2 = TextStyle(fontFamily = AppFont, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 22.4.sp, letterSpacing = 0.56.sp)

val LabelRead = TextStyle(fontFamily = AppFont, fontSize = 16.sp, fontWeight = FontWeight.Medium)
val LabelButton = TextStyle(fontFamily = AppFont, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)

val Annotation1 = TextStyle(fontFamily = AppFont, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp)
val Overline1 = TextStyle(fontFamily = AppFont, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.9.sp, lineHeight = 17.sp)
val SupportingError = TextStyle(fontFamily = AppFont, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = danger_300)

val Typography = Typography(
    displayLarge = Title1.copy(fontSize = 36.sp),
    displayMedium = Title1,
    displaySmall = Title2,

    headlineLarge = Title1.copy(fontSize = 36.sp),
    headlineMedium = Title1,
    headlineSmall = Title2,

    titleLarge = Subtitle1.copy(fontSize = 22.sp),
    titleMedium = Subtitle1,
    titleSmall = Subtitle2,

    bodyLarge = Body1.copy(fontSize = 18.sp),
    bodyMedium = Body1,
    bodySmall = Body2,

    labelLarge = LabelButton,
    labelMedium = LabelRead,
    labelSmall = LabelRead.copy(fontSize = 12.sp),
)

@Preview(widthDp = 600)
@Composable
fun PreviewFonts() {
    AppTheme {
        Surface {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = spacedBy(16.dp)) {
                Text(text = "Display Large", style = MaterialTheme.typography.displayLarge)
                Text(text = "Display Medium", style = MaterialTheme.typography.displayMedium)
                Text(text = "Display Small", style = MaterialTheme.typography.displaySmall)

                Divider()

                Text(text = "Healine Large", style = MaterialTheme.typography.headlineLarge)
                Text(text = "Healine Medium", style = MaterialTheme.typography.headlineMedium)
                Text(text = "Healine Small", style = MaterialTheme.typography.headlineSmall)

                Divider()

                Text(text = "Title Large", style = MaterialTheme.typography.titleLarge)
                Text(text = "Title Medium", style = MaterialTheme.typography.titleMedium)
                Text(text = "Title Small", style = MaterialTheme.typography.titleSmall)

                Divider()

                Text(text = "Body Large", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Body Medium", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Body Small", style = MaterialTheme.typography.bodySmall)

                Divider()

                Text(text = "Label Large", style = MaterialTheme.typography.labelLarge)
                Text(text = "Label Medium", style = MaterialTheme.typography.labelMedium)
                Text(text = "Label Small", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}