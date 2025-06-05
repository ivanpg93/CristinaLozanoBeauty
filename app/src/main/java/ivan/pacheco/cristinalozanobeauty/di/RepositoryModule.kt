package ivan.pacheco.cristinalozanobeauty.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.FindClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.ListClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.UpdateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.repository.ClientDataRepository
import ivan.pacheco.cristinalozanobeauty.presentation.home.CalendarDataRepository
import ivan.pacheco.cristinalozanobeauty.presentation.home.CalendarRepository
import ivan.pacheco.cristinalozanobeauty.presentation.home.GoogleCalendarApi
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.CreateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.DeleteColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.FindColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.ListColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.UpdateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.repository.ColorsHistoryDataRepository
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
    fun providesColorsHistoryRepository(
        listWS: ListColorHistoryWebService,
        findWS: FindColorHistoryWebService,
        createWS: CreateColorHistoryWebService,
        updateWS: UpdateColorHistoryWebService,
        deleteWS: DeleteColorHistoryWebService
    ): ColorsHistoryRepository = ColorsHistoryDataRepository(listWS, findWS, createWS, updateWS, deleteWS)

    @Singleton
    @Provides
    fun provideCalendarRepository(
        googleCalendarApi: GoogleCalendarApi
    ): CalendarRepository = CalendarDataRepository(googleCalendarApi)

}