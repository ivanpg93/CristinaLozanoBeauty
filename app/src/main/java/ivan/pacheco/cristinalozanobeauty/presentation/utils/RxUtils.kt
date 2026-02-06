package ivan.pacheco.cristinalozanobeauty.presentation.utils

import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RxUtils {

    fun <T: Any> Single<T>.toResult(): Single<Result<T>> =
        this.map { Result.success(it) }.onErrorReturn { Result.failure(it) }

    fun <T> Single<T>.applySchedulers(): Single<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun Completable.applySchedulers(): Completable =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun <T> Single<T>.withLoading(loadingLD: MutableLiveData<Boolean>): Single<T> =
        this.doOnSubscribe { loadingLD.value = true }.doFinally { loadingLD.value = false }

    fun Completable.withLoading(loadingLD: MutableLiveData<Boolean>): Completable =
        this.doOnSubscribe { loadingLD.value = true }.doFinally { loadingLD.value = false }

}