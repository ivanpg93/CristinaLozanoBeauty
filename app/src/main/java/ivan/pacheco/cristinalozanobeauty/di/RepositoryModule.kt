package ivan.pacheco.cristinalozanobeauty.di

import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.CreateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.DeleteAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.FindAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.ListAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.UpdateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.repository.AppointmentDataRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientDocumentRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.FindClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.ListClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.UpdateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.repository.ClientDataRepository
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.repository.ClientDocumentDataRepository
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.CreateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.DeleteColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.FindColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.ListColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.UpdateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.repository.ColorsHistoryDataRepository
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import ivan.pacheco.cristinalozanobeauty.core.event.infrastructure.repository.EventDataRepository
import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.repository.AnalyticsRepository
import ivan.pacheco.cristinalozanobeauty.shared.analytics.infrastructure.AnalyticsDataRepository
import ivan.pacheco.cristinalozanobeauty.shared.remote.GoogleCalendarApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesClientRepository(
        listWS: ListClientWebService,
        findWS: FindClientWebService,
        createWS: CreateClientWebService,
        updateWS: UpdateClientWebService,
        deleteWS: DeleteClientWebService
    ): ClientRepository = ClientDataRepository(listWS, findWS, createWS, updateWS, deleteWS)

    @Singleton
    @Provides
    fun providesClientDocumentRepository(): ClientDocumentRepository = ClientDocumentDataRepository()


    @Singleton
    @Provides
    fun providesColorsHistoryRepository(
        listWS: ListColorHistoryWebService,
        findWS: FindColorHistoryWebService,
        createWS: CreateColorHistoryWebService,
        updateWS: UpdateColorHistoryWebService,
        deleteWS: DeleteColorHistoryWebService
    ): ColorsHistoryRepository = ColorsHistoryDataRepository(listWS, findWS, createWS, updateWS, deleteWS)

    @Singleton
    @Provides
    fun providesEventRepository(
        googleCalendarApi: GoogleCalendarApi
    ): EventRepository = EventDataRepository(googleCalendarApi)

    @Singleton
    @Provides
    fun providesAppointmentRepository(
        listWS: ListAppointmentWebService,
        findWS: FindAppointmentWebService,
        createWS: CreateAppointmentWebService,
        updateWS: UpdateAppointmentWebService,
        deleteWS: DeleteAppointmentWebService,
        analyticsRepository: AnalyticsRepository
    ): AppointmentRepository = AppointmentDataRepository(listWS, findWS, createWS, updateWS, deleteWS, analyticsRepository)

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        firebaseAnalytics: FirebaseAnalytics
    ): AnalyticsRepository = AnalyticsDataRepository(firebaseAnalytics)

}