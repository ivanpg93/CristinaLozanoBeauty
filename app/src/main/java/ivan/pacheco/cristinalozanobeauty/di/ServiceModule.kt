package ivan.pacheco.cristinalozanobeauty.di

import android.content.Context
import androidx.work.WorkManager
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ivan.pacheco.cristinalozanobeauty.shared.remote.API
import ivan.pacheco.cristinalozanobeauty.shared.remote.GoogleCalendarApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun providesWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

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

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)

}