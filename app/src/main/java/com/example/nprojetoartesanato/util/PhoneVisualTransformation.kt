package com.example.nprojetoartesanato.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0, 11) else text.text
        val out = StringBuilder()

        for (i in trimmed.indices) {
            when (i) {
                0 -> out.append("(").append(trimmed[i])
                1 -> out.append(trimmed[i]).append(") ")
                5 -> {
                    if (trimmed.length <= 10) {
                        out.append(trimmed[i])
                    } else {
                        out.append(trimmed[i])
                    }
                }
                6 -> {
                    if (trimmed.length <= 10) {
                        out.append("-").append(trimmed[i])
                    } else {
                        out.append(trimmed[i])
                    }
                }
                7 -> {
                    if (trimmed.length > 10) {
                        out.append("-").append(trimmed[i])
                    } else {
                        out.append(trimmed[i])
                    }
                }
                else -> out.append(trimmed[i])
            }
        }

        val phoneOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val textLength = trimmed.length
                if (offset >= textLength) return out.length

                val isCell = textLength > 10
                return when {
                    offset <= 2 -> offset + 1 // after '('
                    offset <= 6 -> offset + 3 // after ') '
                    isCell && offset <= 7 -> offset + 3
                    else -> offset + 4 // after '-'
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val textLength = trimmed.length
                val isCell = textLength > 10
                return when {
                    offset <= 1 -> 0
                    offset <= 4 -> offset - 1
                    offset <= 9 -> offset - 3
                    isCell && offset <= 10 -> offset - 3
                    else -> (offset - 4).coerceAtMost(textLength)
                }
            }
        }

        return TransformedText(AnnotatedString(out.toString()), phoneOffsetTranslator)
    }
}
