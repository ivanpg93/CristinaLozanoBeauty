package ivan.pacheco.cristinalozanobeauty.presentation.client.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
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

    // LiveData
    override val navigationLD = MutableLiveData<Destination>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    // Actions
    fun actionSave(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        birthdate: Date?,
        profession: String,
        town: String,
        nailDisorderList: List<NailDisorder>,
        skinDisorderList: List<SkinDisorder>,
        treatment: String,
        allergy: String,
        diabetes: Boolean,
        poorCoagulation: Boolean
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
            allergy,
            diabetes,
            poorCoagulation
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableCompletableObserver() {
                override fun onComplete() { navigationLD.value = Destination.Back }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_form_error_create }
            })
    }

}