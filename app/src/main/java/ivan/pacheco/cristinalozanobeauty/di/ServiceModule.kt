package ivan.pacheco.cristinalozanobeauty.di

import android.app.Application
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ivan.pacheco.cristinalozanobeauty.core.shared.API
import ivan.pacheco.cristinalozanobeauty.core.shared.GoogleCalendarApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun providesWorkManager(application: Application): WorkManager =
        WorkManager.getInstance(application)

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API.GOOGLE_CALENDAR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // Para que funcione con Rx
            .build()
    }

    @Singleton
    @Provides
    fun provideGoogleCalendarApi(retrofit: Retrofit): GoogleCalendarApi = retrofit.create(GoogleCalendarApi::class.java)

}