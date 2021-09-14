package ru.ksart.potatohandbook.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.ksart.potatohandbook.model.db.PotatoDaos
import ru.ksart.potatohandbook.model.db.PotatoDatabase
import ru.ksart.potatohandbook.model.db.PotatoDatabaseVersion
import ru.ksart.potatohandbook.model.db.cursor.PotatoDatabaseCursorImpl
import ru.ksart.potatohandbook.model.db.room.PotatoDatabaseRoomImpl
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @PotatoDatabaseRoom
    @Provides
    @Singleton
    fun provideDatabaseRoom(@ApplicationContext context: Context): PotatoDatabase {
        return Room.databaseBuilder(
            context,
            PotatoDatabaseRoomImpl::class.java,
            PotatoDatabaseVersion.DB_NAME
        )
            // пересоздание схемы базы данных с уничтожением данных, без миграции, данные будут потеряны
            .fallbackToDestructiveMigration()
            .build()
    }

    @PotatoDatabaseCursor
    @Provides
    @Singleton
    fun provideDatabaseCursor(@ApplicationContext context: Context): PotatoDatabase {
        return PotatoDatabaseCursorImpl(context)
    }

    @Provides
    @Singleton
    fun provideDao(
        @PotatoDatabaseRoom roomDb: PotatoDatabase,
        @PotatoDatabaseCursor cursorDb: PotatoDatabase,
    ): PotatoDaos {
        return PotatoDaos(roomDb, cursorDb)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PotatoDatabaseRoom

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PotatoDatabaseCursor
