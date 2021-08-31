package ru.ksart.potatohandbook.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ru.ksart.potatohandbook.model.repository.PotatoRepository
import ru.ksart.potatohandbook.model.repository.PotatoRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    @ViewModelScoped
    abstract fun provideRouteRepository(impl: PotatoRepositoryImpl): PotatoRepository
}
