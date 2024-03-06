package br.com.trybu.ui.widget.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class TextFieldMask internal constructor(
    val pattern: String,
    val placeholder: Char = '#'
) : VisualTransformation {

    val maxTextLength by lazy { pattern.count { it == placeholder } }

    private val maskedIndexes: List<IndexedValue<Char>> = run {
        var masks = 0
        pattern.mapIndexedNotNull { index, c ->
            if (c == placeholder) {
                null
            } else {
                IndexedValue(index - masks, c).also { masks += 1 }
            }
        }
    }

    fun transform(original: String): String {
        val iterator = original.iterator()
        return pattern.mapNotNull { patternChar ->
            when {
                !iterator.hasNext() -> null
                patternChar == placeholder -> "${iterator.nextChar()}"
                else -> "$patternChar"
            }
        }.joinToString("")
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = transform(text.text)
        return TransformedText(
            text = AnnotatedString(transformed),
            offsetMapping = object : OffsetMapping {

                override fun originalToTransformed(offset: Int): Int {
                    if (text.text.isEmpty()) return offset
                    return (offset + maskedIndexes.count { offset > it.index })
                        .takeIf { it <= pattern.length } ?: pattern.length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (text.text.isEmpty()) return offset
                    return (offset - maskedIndexes.count { offset > it.index })
                        .takeIf { it <= maxTextLength } ?: maxTextLength
                }
            }
        )
    }
}

fun mask(pattern: String, placeholder: Char = '#') = TextFieldMask(pattern, placeholder)