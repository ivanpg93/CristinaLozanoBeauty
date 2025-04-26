package ivan.pacheco.cristinalozanobeauty.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.databinding.HomeFragmentBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val vm: HomeViewModel by viewModels()

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private val SIGN_IN_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoogleSignIn()
        silentSignIn(requireContext())

        setupCalendar()

        vm.getEventsLD().observe(viewLifecycleOwner) { events ->
            events.forEach { event ->
                // Aquí quieres marcar el día del calendario
                // De momento, simplemente imprimimos en log:
                println("Evento: ${event.summary} desde ${event.startDateTime} hasta ${event.endDateTime}")
                // Luego mejoramos para pintar días en el CalendarView
            }
        }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error)}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCalendar() {
        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val monthFormatted = String.format("%02d", month + 1) // CalendarView: month empieza en 0
            val dayFormatted = String.format("%02d", dayOfMonth)
            val date = "$year-$monthFormatted-$dayFormatted"
            vm.onDateSelected(date)
        }
    }

    private fun initGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("217437853819-ohqt67v4ioejvkjvkha2dlmj6gh1sr36.apps.googleusercontent.com")
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
            .build()
    }

    private fun silentSignIn(context: Context) {
        val client = GoogleSignIn.getClient(context, googleSignInOptions)
        val task = client.silentSignIn()

        if (task.isSuccessful) {
            val account = task.result
            vm.onGoogleAccountReady(account)
        } else {
            task.addOnCompleteListener { completedTask ->
                try {
                    val account = completedTask.getResult(ApiException::class.java)
                    vm.onGoogleAccountReady(account)
                } catch (e: ApiException) {
                    val signInIntent = client.signInIntent
                    startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                vm.onGoogleAccountReady(account)
            } catch (e: ApiException) {
                // Aquí decides qué hacer, por ejemplo mostrar un mensaje de error
            }
        }
    }

}