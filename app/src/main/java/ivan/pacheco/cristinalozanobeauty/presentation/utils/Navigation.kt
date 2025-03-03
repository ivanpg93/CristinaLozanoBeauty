package ivan.pacheco.cristinalozanobeauty.presentation.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

sealed class Destination {
    data object Back : Destination()
    data object Home : Destination()
    data object ClientList : Destination()
    data class ClientDetail(val clientId: String) : Destination()
    data object Message : Destination()
}

interface Navigation {
    val navigationLD: MutableLiveData<Destination>
    fun getNavigationLD(): LiveData<Destination> = navigationLD
}