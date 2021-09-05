package ru.ksart.potatohandbook.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.ksart.potatohandbook.model.db.DbMs
import ru.ksart.potatohandbook.model.db.PotatoDao
import ru.ksart.potatohandbook.model.db.PotatoDatabase
import ru.ksart.potatohandbook.model.db.PotatoDatabaseVersion
import ru.ksart.potatohandbook.model.db.room.PotatoDatabaseRoomImpl
import ru.ksart.potatohandbook.utils.DebugHelper
import javax.inject.Inject
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
        return Room.databaseBuilder(
            context,
            PotatoDatabaseRoomImpl::class.java,
            PotatoDatabaseVersion.DB_NAME
        )
            // пересоздание схемы базы данных с уничтожением данных, без миграции, данные будут потеряны
            .fallbackToDestructiveMigration()
            .build()
    }

/*
    @Singleton
    class PotatoDatabaseDao @Inject constructor(
        @PotatoDatabaseRoom private val roomDb: PotatoDatabase,
        @PotatoDatabaseCursor private val cursorDb: PotatoDatabase,
    ) {
        fun potatoDao(isRoom: Boolean): PotatoDao {
            return if (isRoom) roomDb.potatoDao()
            else cursorDb.potatoDao()
        }

    }
*/

    @Provides
    @Singleton
    fun provideDao(
        @PotatoDatabaseRoom roomDb: PotatoDatabase,
        @PotatoDatabaseCursor cursorDb: PotatoDatabase,
    ): PotatoDao = when (PotatoDatabaseVersion.useDbMs) {
        DbMs.Room -> roomDb.potatoDao().also {
            DebugHelper.log("provideDao use Room")
        }
        DbMs.Cursor -> cursorDb.potatoDao().also {
            DebugHelper.log("provideDao use Cursor")
        }
    }

/*
    fun getDao(isRoom: Boolean = true): PotatoDao {
        @PotatoDatabaseRoom
        lateinit var  roomDb: PotatoDatabase

        @PotatoDatabaseCursor
        lateinit var  cursorDb: PotatoDatabase

        return if (isRoom) roomDb.potatoDao()
        else cursorDb.potatoDao()
    }
*/
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PotatoDatabaseRoom

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PotatoDatabaseCursor

/*
class MyDao @Inject constructor(
    @PotatoDatabaseRoom private val roomDb: PotatoDatabase,
    @PotatoDatabaseCursor private val cursorDb: PotatoDatabase,
) {

    fun getDao(isRoom: Boolean = true): PotatoDao {
        return if (isRoom) roomDb.potatoDao()
        else cursorDb.potatoDao()
    }

}
*/
