package ivan.pacheco.cristinalozanobeauty.presentation.message

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.core.message.application.usecase.SendMessageUC
import javax.inject.Inject

@HiltViewModel
class MessageViewModel@Inject constructor(private val sendMessageUC: SendMessageUC): ViewModel() {

    fun actionSendMessage(name: String, telephone: String, day: Int, month: Int, year: Int, hour: Int, minute: Int) {
        sendMessageUC.execute(name, telephone, day, month, year, hour, minute)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() { }
                override fun onError(e: Throwable) { }
            })
    }

}