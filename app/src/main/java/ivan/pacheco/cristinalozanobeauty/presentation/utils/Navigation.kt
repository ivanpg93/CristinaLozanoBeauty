package ivan.pacheco.cristinalozanobeauty.presentation.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

sealed class Destination {
    data object Back : Destination()
    data object Home : Destination()
    data object ClientList : Destination()
    data class ClientDetail(val clientId: String) : Destination()
    data object ClientForm : Destination()
    data object Message : Destination()
    data class ColorHistoryList(val clientId: String) : Destination()
    data class ColorHistoryDetail(val clientId: String, val colorId: String) : Destination()
    data class ColorHistoryForm(val clientId: String) : Destination()
    data class AppointmentHistoryList(val clientId: String) : Destination()
}

interface Navigation {
    val navigationLD: MutableLiveData<Destination>
    fun getNavigationLD(): LiveData<Destination> = navigationLD
}