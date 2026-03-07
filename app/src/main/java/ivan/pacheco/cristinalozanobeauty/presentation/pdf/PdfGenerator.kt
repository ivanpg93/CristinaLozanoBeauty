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
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils
import java.io.File
import java.util.Locale.getDefault

class PdfGenerator(
    private val context: Context
) {

    data class Tutor(
        val fullName: String,
        val dni: String,
        val relation: String
    )

    private companion object {
        const val CONSENT = "consentimiento_"
        const val BASE_PDF = "_base.pdf"
    }

    fun generateUnsignedPdf(client: Client, tutor: Tutor): File {

        // Create pdf
        val file = File(context.filesDir, "$CONSENT${client.firstName}_${client.lastName}$BASE_PDF")
        val pdf = PdfDocument(PdfWriter(file))
        val document = Document(pdf)

        // Title
        document.add(
            Paragraph(
                context.getString(R.string.pdf_sign_title)
                    .uppercase(getDefault())
            ).setFontSize(22f)
        )

        // Spacer
        document.add(Paragraph("\n"))

        // Minor data
        document.add(sectionTitle(context.getString(R.string.pdf_sign_minor_section_title)))
        document.add(info(context.getString(R.string.pdf_sign_minor_name), "${client.firstName} ${client.lastName}"))
        document.add(info(context.getString(R.string.pdf_sign_minor_birthday), client.birthday?.let { DateUtils.formatDate(it) } ?: "-"))
        document.add(info(context.getString(R.string.pdf_sign_minor_phone), client.phone))
        document.add(info(context.getString(R.string.pdf_sign_minor_email), client.email))
        document.add(info(context.getString(R.string.pdf_sign_minor_town), client.town))

        // Tutor auth
        document.add(sectionTitle(context.getString(R.string.pdf_sign_tutor_dialog_title)))
        document.add(info(context.getString(R.string.pdf_sign_tutor_fullname), tutor.fullName))
        document.add(info(context.getString(R.string.pdf_sign_tutor_nif), tutor.dni))
        document.add(info(context.getString(R.string.pdf_sign_tutor_relation), tutor.relation))

        // Spacer
        document.add(Paragraph("\n"))

        // Authorization text
        document.add(
            Paragraph(context.getString(R.string.pdf_sign_authorization_text)).setFontSize(11f)
        )

        // Spacer
        document.add(Paragraph("\n\n"))

        // Signature
        document.add(Paragraph(context.getString(R.string.pdf_sign_signature)).setFontSize(16f))

        // Spacer
        document.add(Paragraph("\n\n\n\n"))

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
                50f,
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

    private fun sectionTitle(text: String) =
        Paragraph(text)
            .setFontSize(16f)
            .setMarginTop(12f)

    private fun info(label: String, value: String) = Paragraph("$label: $value").setFontSize(12f)


}