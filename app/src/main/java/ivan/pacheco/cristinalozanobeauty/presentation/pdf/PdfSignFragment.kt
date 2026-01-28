package ivan.pacheco.cristinalozanobeauty.presentation.pdf

import android.content.ContentValues
import android.graphics.pdf.PdfRenderer
import android.net.Uri
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

    private companion object {
        const val APPLICATION_PDF = "application/pdf"
    }

    private lateinit var renderer: PdfRenderer
    private lateinit var parcelFileDescriptor: ParcelFileDescriptor
    private val args: PdfSignFragmentArgs by navArgs()
    private val vm: PdfSignViewModel by viewModels()
    private lateinit var basePdf: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentPdfSignBinding.bind(view)
        val pdfGenerator = PdfGenerator(requireContext())


        vm.actionLoadClient(args.clientId)

        // Navigation
        vm.navigationLD.observe(viewLifecycleOwner) { destination -> navigate(destination) }

        // Client
        vm.getClientLD().observe(viewLifecycleOwner) { client ->
            basePdf = pdfGenerator.generateUnsignedPdf(client)
            val pfd = ParcelFileDescriptor.open(basePdf, ParcelFileDescriptor.MODE_READ_ONLY)
            renderPdfFromPfd(binding, pfd)
        }

        // Clear signature
        binding.btnClear.setOnClickListener { binding.signaturePad.clear() }

        // Sign document
        binding.btnSign.setOnClickListener {
            if (binding.signaturePad.isEmpty) {
                showAlert(R.string.pdf_sign_mandatory_signature)
                return@setOnClickListener
            }

            val signedPdf = pdfGenerator.generateSignedPdf(
                basePdf,
                binding.signaturePad.signatureBitmap
            )

            val pdfUri = Uri.fromFile(signedPdf)
            vm.actionSaveSignedPdf(args.clientId, pdfUri)

            openPdf(signedPdf)
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        if (::renderer.isInitialized) { renderer.close() }
        if (::parcelFileDescriptor.isInitialized) { parcelFileDescriptor.close() }
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

    private fun navigate(destination: Destination) {
        when(destination) {
            is Destination.Back -> findNavController().popBackStack()
            else -> {} // Do nothing
        }
    }

}
