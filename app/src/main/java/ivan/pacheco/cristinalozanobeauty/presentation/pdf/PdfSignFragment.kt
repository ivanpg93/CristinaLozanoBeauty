package ivan.pacheco.cristinalozanobeauty.presentation.pdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentPdfSignBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.openPdf
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showAlert
import java.io.File

@AndroidEntryPoint
class PdfSignFragment: Fragment() {

    private var _binding: FragmentPdfSignBinding? = null
    private val binding get() = _binding!!
    private val vm: PdfSignViewModel by viewModels()
    private lateinit var pdfGenerator: PdfGenerator
    private lateinit var renderer: PdfRenderer
    private lateinit var parcelFileDescriptor: ParcelFileDescriptor
    private lateinit var basePdf: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPdfSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pdfGenerator = PdfGenerator(requireContext())

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
        binding.btnSign.setOnClickListener { signDocument() }
    }

    override fun onDestroyView() {
        if (::renderer.isInitialized) { renderer.close() }
        if (::parcelFileDescriptor.isInitialized) { parcelFileDescriptor.close() }
        super.onDestroyView()
    }

    private fun signDocument() {
        if (binding.signaturePad.isEmpty) {
            showAlert(R.string.pdf_sign_mandatory_signature)
            return
        }

        val signedPdf = pdfGenerator.generateSignedPdf(
            basePdf,
            binding.signaturePad.signatureBitmap
        )

        val pdfUri = Uri.fromFile(signedPdf)
        vm.actionSaveSignedPdf(pdfUri)

        openPdf(signedPdf)
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
