package ivan.pacheco.cristinalozanobeauty.presentation.client.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.CreateClientUC
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.NailDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.SkinDisorder
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ClientFormViewModel @Inject constructor(
    private val uc: CreateClientUC
): ViewModel(), Navigation {

    override val navigationLD = MutableLiveData<Destination>()
    private val errorLD = MutableLiveData<String>()
    fun getErrorLD(): LiveData<String> = errorLD

    fun actionSave(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        birthdate: Date,
        profession: String,
        town: String,
        nailDisorderList: List<NailDisorder>,
        skinDisorderList: List<SkinDisorder>,
        treatment: String,
        allergy: String
    ) {
        uc.execute(
            firstName,
            lastName,
            phone,
            email,
            birthdate,
            profession,
            town,
            nailDisorderList,
            skinDisorderList,
            treatment,
            allergy
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: DisposableCompletableObserver() {
                override fun onComplete() { navigationLD.value = Destination.Back }
                override fun onError(e: Throwable) { errorLD.value = "No se ha podido registrar la clienta" }
            })
    }

}