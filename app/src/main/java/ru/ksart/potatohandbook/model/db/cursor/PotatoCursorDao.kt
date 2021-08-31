package ru.ksart.potatohandbook.model.db.cursor

import kotlinx.coroutines.flow.Flow
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.db.PotatoDao

class PotatoCursorDao: PotatoDao {
    override fun getPotatoAll(): Flow<List<Potato>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPotato(potato: Potato): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updatePotato(potato: Potato) {
        TODO("Not yet implemented")
    }

    override suspend fun removePotato(potato: Potato) {
        TODO("Not yet implemented")
    }
}