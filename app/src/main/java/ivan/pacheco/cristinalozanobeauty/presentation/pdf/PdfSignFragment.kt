package ivan.pacheco.cristinalozanobeauty.presentation.pdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentPdfSignBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.isCorrectNifOrNie
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

        // Client — show tutor dialog when client is loaded
        vm.getClientLD().observe(viewLifecycleOwner) { client -> showTutorDialog(client) }

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

    private fun showTutorDialog(client: Client) {
        val context = requireContext()
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_tutor_data, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.et_tutor_name_text)
        val dniInput = dialogView.findViewById<EditText>(R.id.et_tutor_dni_text)
        val relationInput = dialogView.findViewById<EditText>(R.id.et_tutor_relation_text)

        // Relation selector — single choice dialog
        val relations = resources.getStringArray(R.array.tutor_relations)
        var selectedRelationIndex = -1

        relationInput.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(getString(R.string.pdf_sign_tutor_select_relation))
                .setSingleChoiceItems(relations, selectedRelationIndex) { _, index ->
                    selectedRelationIndex = index
                }
                .setPositiveButton(getString(R.string.accept)) { dialogInterface, _ ->
                    if (selectedRelationIndex >= 0) {
                        relationInput.setText(relations[selectedRelationIndex])
                    }
                    dialogInterface.dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            dialog.setOnShowListener {
                val goldColor = ContextCompat.getColor(context, R.color.gold)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(goldColor)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(goldColor)
            }

            dialog.show()
        }

        // Main tutor dialog
        val tutorDialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.pdf_sign_tutor_dialog_title))
            .setView(dialogView)
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.accept), null)
            .create()

        tutorDialog.setOnShowListener {
            val goldColor = ContextCompat.getColor(context, R.color.gold)
            tutorDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(goldColor)
            tutorDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(goldColor)

            // Cancel — navigate back
            tutorDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                tutorDialog.dismiss()
                findNavController().popBackStack()
            }

            // Accept — validate and generate PDF
            tutorDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = nameInput.text.toString().trim()
                val dni = dniInput.text.toString().trim()
                val relation = relationInput.text.toString().trim()

                if (name.isBlank() || dni.isBlank() || relation.isBlank()) {
                    showAlert(R.string.pdf_sign_tutor_mandatory_fields)
                    return@setOnClickListener
                }

                if (!dniInput.isCorrectNifOrNie()) {
                    showAlert(R.string.pdf_sign_tutor_error_nif)
                    return@setOnClickListener
                }

                val tutor = PdfGenerator.Tutor(
                    fullName = name,
                    dni = dni,
                    relation = relation
                )

                basePdf = pdfGenerator.generateUnsignedPdf(client, tutor)
                val pfd = ParcelFileDescriptor.open(basePdf, ParcelFileDescriptor.MODE_READ_ONLY)
                renderPdfFromPfd(binding, pfd)

                tutorDialog.dismiss()
            }
        }

        tutorDialog.show()
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
