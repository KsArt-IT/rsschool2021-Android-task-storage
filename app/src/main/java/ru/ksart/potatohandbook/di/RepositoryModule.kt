package ru.ksart.potatohandbook.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ksart.potatohandbook.model.repository.PotatoRepository
import ru.ksart.potatohandbook.model.repository.PotatoRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideRouteRepository(impl: PotatoRepositoryImpl): PotatoRepository
}
