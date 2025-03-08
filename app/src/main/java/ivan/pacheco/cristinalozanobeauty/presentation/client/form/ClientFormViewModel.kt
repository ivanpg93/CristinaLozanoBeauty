package ivan.pacheco.cristinalozanobeauty.presentation.client.form

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import javax.inject.Inject

@HiltViewModel
class ClientFormViewModel @Inject constructor(): ViewModel(), Navigation {

    override val navigationLD = MutableLiveData<Destination>()

    fun actionSave(name: String, lastName: String, phone: String, email: String) {
        navigationLD.value = Destination.Back
    }

}