package ivan.pacheco.cristinalozanobeauty.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.CreateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.DeleteAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.FindAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.ListAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.UpdateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.CreateAppointmentWS
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.DeleteAppointmentWS
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.FindAppointmentWS
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.ListAppointmentWS
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.UpdateAppointmentWS
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.FindClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.ListClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.UpdateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.CreateClientWS
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.DeleteClientWS
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.FindClientWS
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.ListClientWS
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.UpdateClientWS
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.CreateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.DeleteColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.FindColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.ListColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.UpdateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice.CreateColorHistoryWS
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice.DeleteColorHistoryWS
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice.FindColorHistoryWS
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice.ListColorHistoryWS
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice.UpdateColorHistoryWS
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebServiceModule {

    // Client
    @Singleton
    @Provides
    fun providesListClientWebService(): ListClientWebService = ListClientWS()

    @Singleton
    @Provides
    fun providesFindClientWebService(): FindClientWebService = FindClientWS()

    @Singleton
    @Provides
    fun providesCreateClientWebService(): CreateClientWebService = CreateClientWS()

    @Singleton
    @Provides
    fun providesUpdateClientWebService(): UpdateClientWebService = UpdateClientWS()

    @Singleton
    @Provides
    fun providesDeleteClientWebService(): DeleteClientWebService = DeleteClientWS()

    // Color history
    @Singleton
    @Provides
    fun providesListColorHistoryWebService(): ListColorHistoryWebService = ListColorHistoryWS()

    @Singleton
    @Provides
    fun providesFindColorHistoryWebService(): FindColorHistoryWebService = FindColorHistoryWS()

    @Singleton
    @Provides
    fun providesCreateColorHistoryWebService(): CreateColorHistoryWebService = CreateColorHistoryWS()

    @Singleton
    @Provides
    fun providesUpdateColorHistoryWebService(): UpdateColorHistoryWebService = UpdateColorHistoryWS()

    @Singleton
    @Provides
    fun providesDeleteColorHistoryWebService(): DeleteColorHistoryWebService = DeleteColorHistoryWS()

    // Appointment
    @Singleton
    @Provides
    fun providesListAppointmentWebService(): ListAppointmentWebService = ListAppointmentWS()

    @Singleton
    @Provides
    fun providesFindAppointmentWebService(): FindAppointmentWebService = FindAppointmentWS()

    @Singleton
    @Provides
    fun providesCreateAppointmentWebService(): CreateAppointmentWebService = CreateAppointmentWS()

    @Singleton
    @Provides
    fun providesUpdateAppointmentWebService(): UpdateAppointmentWebService = UpdateAppointmentWS()

    @Singleton
    @Provides
    fun providesDeleteAppointmentWebService(): DeleteAppointmentWebService = DeleteAppointmentWS()

}