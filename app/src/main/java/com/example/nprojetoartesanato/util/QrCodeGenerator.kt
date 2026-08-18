package com.example.nprojetoartesanato.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

class QrCodeGenerator {

    fun generate(
        content: String,
        width: Int = 512,
        height: Int = 512
    ): Bitmap {

        require(content.isNotBlank()) {
            "O conteúdo do QR Code não pode ser vazio."
        }

        val hints = mapOf(
            EncodeHintType.MARGIN to 1
        )

        val bitMatrix = try {
            MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )
        } catch (exception: WriterException) {
            throw IllegalArgumentException(
                "Não foi possível gerar o QR Code.",
                exception
            )
        }

        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.RGB_565
        )

        for (x in 0 until width) {
            for (y in 0 until height) {

                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) {
                        android.graphics.Color.BLACK
                    } else {
                        android.graphics.Color.WHITE
                    }
                )
            }
        }

        return bitmap
    }
}