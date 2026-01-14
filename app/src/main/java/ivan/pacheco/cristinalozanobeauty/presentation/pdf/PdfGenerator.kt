package ivan.pacheco.cristinalozanobeauty.presentation.pdf

import android.content.Context
import android.graphics.Bitmap
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toFormattedString
import java.io.File
import java.time.LocalDate
import java.util.Locale.getDefault

class PdfGenerator(
    private val context: Context
) {

    private companion object {
        const val CONSENT = "consentimiento_"
        const val BASE_PDF = "_base.pdf"
    }

    fun generateUnsignedPdf(client: Client): File {
        val file = File(context.filesDir, "$CONSENT${client.firstName}_${client.lastName}$BASE_PDF")

        val pdf = PdfDocument(PdfWriter(file))
        val document = Document(pdf)

        val titleStr = context.getString(R.string.pdf_sign_title).uppercase(getDefault())
        document.add(Paragraph(titleStr).setFontSize(24f))

        val clientStr = context.getString(R.string.pdf_sign_client)
        document.add(Paragraph("$clientStr: ${client.firstName} ${client.lastName}").setFontSize(18f))

        val dateStr = context.getString(R.string.pdf_sign_date)
        document.add(Paragraph("$dateStr: ${LocalDate.now().toFormattedString()}").setFontSize(18f))

        document.add(Paragraph().setMarginTop(200f))

        val signatureStr = context.getString(R.string.pdf_sign_signature)
        document.add(Paragraph("$signatureStr:").setFontSize(18f))

        document.close()
        return file
    }

    fun generateSignedPdf(
        basePdf: File,
        signatureBitmap: Bitmap
    ): File {

        val output = File(
            context.filesDir,
            basePdf.name.replace("_base", "_signed")
        )

        val pdf = PdfDocument(
            com.itextpdf.kernel.pdf.PdfReader(basePdf),
            PdfWriter(output)
        )

        val document = Document(pdf)

        val image = Image(ImageDataFactory.create(signatureBitmap.toByteArray()))
            .setAutoScale(true)
            .setFixedPosition(
                pdf.numberOfPages,
                100f,
                120f,
                200f
            )

        document.add(image)
        document.close()

        return output
    }

    private fun Bitmap.toByteArray(): ByteArray = ByteArrayOutputStream().use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
        it.toByteArray()
    }

}