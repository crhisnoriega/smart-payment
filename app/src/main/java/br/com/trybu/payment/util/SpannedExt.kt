package br.com.trybu.payment.util

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat
import br.com.trybu.payment.data.model.RetrieveOperationsResponse

fun RetrieveOperationsResponse.Operation.TransactionType.toPaymentType() = when {
    this.paymentType == 2 && this.installmentsNumber == 1 -> "Credito - "
    this.paymentType == 2 -> "Credito Parcelado ${this.installmentsNumber}x\n"
    this.paymentType == 3 -> "Credito - "
    this.paymentType == 4 -> "Debito - "
    else -> "Debito - "
}

fun String?.toAnnotatedString(): AnnotatedString = buildAnnotatedString {

    val spannableString = SpannableStringBuilder(this@toAnnotatedString).toString()
    val spanned = HtmlCompat.fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_COMPACT)

    append(spanned.toString())
    spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = spanned.getSpanStart(span)
        val end = spanned.getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ), start, end
                )
            }

            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline),
                start,
                end
            )

            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)),
                start,
                end
            )
        }
    }
}