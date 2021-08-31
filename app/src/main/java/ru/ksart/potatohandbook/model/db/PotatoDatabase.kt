package ru.ksart.potatohandbook.model.db

import ru.ksart.potatohandbook.model.db.room.PotatoRoomDao

interface PotatoDatabase {
    fun potatoDao(): PotatoDao
}