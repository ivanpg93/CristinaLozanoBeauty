package ivan.pacheco.cristinalozanobeauty.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.ListClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.CreateClientWS
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.DeleteClientWS
import ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice.ListClientWS
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebServiceModule {

    @Singleton
    @Provides
    fun providesCreateClientWebService(): CreateClientWebService = CreateClientWS()

    @Singleton
    @Provides
    fun providesListClientWebService(): ListClientWebService = ListClientWS()

    @Singleton
    @Provides
    fun providesDeleteClientWebService(): DeleteClientWebService = DeleteClientWS()

}