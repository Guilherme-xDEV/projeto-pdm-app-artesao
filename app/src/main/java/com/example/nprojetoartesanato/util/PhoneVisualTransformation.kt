package com.example.nprojetoartesanato.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = if (text.text.length > 11) text.text.substring(0, 11) else text.text

        // Build formatted string and precompute mapping tables
        val formatted = StringBuilder()
        // originalToTransformedMap[i] = position in formatted string for original index i
        val originalToTransformed = IntArray(raw.length + 1)
        originalToTransformed[0] = 0

        for (i in raw.indices) {
            when (i) {
                0 -> {
                    formatted.append("(")
                    formatted.append(raw[i])
                }
                1 -> {
                    formatted.append(raw[i])
                    formatted.append(") ")
                }
                5 -> {
                    formatted.append(raw[i])
                    if (raw.length <= 10) {
                        formatted.append("-")
                    }
                }
                6 -> {
                    formatted.append(raw[i])
                    if (raw.length > 10) {
                        formatted.append("-")
                    }
                }
                else -> {
                    formatted.append(raw[i])
                }
            }
            originalToTransformed[i + 1] = formatted.length
        }

        // transformedToOriginalMap[j] = original index for transformed position j
        val transformedToOriginal = IntArray(formatted.length + 1)
        var origIndex = 0
        for (j in 0..formatted.length) {
            while (origIndex < raw.length && originalToTransformed[origIndex + 1] <= j) {
                origIndex++
            }
            transformedToOriginal[j] = origIndex
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                return originalToTransformed[clamped]
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                return transformedToOriginal[clamped]
            }
        }

        return TransformedText(AnnotatedString(formatted.toString()), offsetTranslator)
    }
}
