package ivan.pacheco.cristinalozanobeauty.presentation.pdf

import android.content.ContentValues
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentPdfSignBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.openPdf
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showAlert
import java.io.File

@AndroidEntryPoint
class PdfSignFragment : Fragment(R.layout.fragment_pdf_sign) {

    private lateinit var renderer: PdfRenderer
    private lateinit var parcelFileDescriptor: ParcelFileDescriptor
    private val args: PdfSignFragmentArgs by navArgs()
    private val vm: PdfSignViewModel by viewModels()
    private lateinit var basePdf: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentPdfSignBinding.bind(view)
        vm.actionLoadClient(args.clientId)

        val pdfGenerator = PdfGenerator(requireContext())

        // Navigation
        vm.navigationLD.observe(viewLifecycleOwner) { destination -> navigate(destination) }

        vm.getClientLD().observe(viewLifecycleOwner) { client ->
            val hasSignedPdf = client.minorUrlDocument.isNotBlank()
            binding.signaturePad.visibility = if (hasSignedPdf) View.GONE else View.VISIBLE
            binding.pdfActions.visibility = if (hasSignedPdf) View.GONE else View.VISIBLE

            val pfd: ParcelFileDescriptor? = if (hasSignedPdf) {
                try {
                    val uri = client.minorUrlDocument.toUri()
                    requireContext().contentResolver.openFileDescriptor(uri, "r")
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } else {
                basePdf = pdfGenerator.generateUnsignedPdf(client)
                ParcelFileDescriptor.open(basePdf, ParcelFileDescriptor.MODE_READ_ONLY)
            }

            pfd?.let {
                renderPdfFromPfd(binding, it)
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "No se pudo cargar el PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnClear.setOnClickListener {
            binding.signaturePad.clear()
        }

        binding.btnSign.setOnClickListener {
            if (binding.signaturePad.isEmpty) {
                showAlert(R.string.pdf_sign_mandatory_signature)
                return@setOnClickListener
            }

            val signedPdf = pdfGenerator.generateSignedPdf(
                basePdf,
                binding.signaturePad.signatureBitmap
            )

            val pdfUri = savePdfToDocuments(signedPdf)

            if (pdfUri != null) {
                vm.actionSaveSignedPdf(args.clientId, pdfUri)

                openPdf(signedPdf)
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al guardar PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        renderer.close()
        parcelFileDescriptor.close()
        super.onDestroyView()
    }

    private fun renderPdfFromPfd(
        binding: FragmentPdfSignBinding,
        pfd: ParcelFileDescriptor
    ) {
        renderer = PdfRenderer(pfd)
        parcelFileDescriptor = pfd

        binding.pdfView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = PdfPageAdapter(renderer)
        }
    }

    private fun savePdfToDocuments(file: File): String? {
        return try {
            val resolver = requireContext().contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }

            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            uri?.let {
                resolver.openOutputStream(it)?.use { out ->
                    file.inputStream().copyTo(out)
                }
                it.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun navigate(destination: Destination) {
        when(destination) {
            is Destination.Back -> findNavController().popBackStack()
            else -> {} // Do nothing
        }
    }

}
