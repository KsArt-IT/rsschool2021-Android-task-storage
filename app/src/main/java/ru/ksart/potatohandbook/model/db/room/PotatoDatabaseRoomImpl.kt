package ru.ksart.potatohandbook.model.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.db.PotatoDatabase
import ru.ksart.potatohandbook.model.db.PotatoDatabaseVersion

@Database(
    entities = [Potato::class],
    exportSchema = false,
    version = PotatoDatabaseVersion.DB_VERSION
)
abstract class PotatoDatabaseRoomImpl : RoomDatabase(), PotatoDatabase {

    abstract override fun potatoDao(): PotatoRoomDao
}
