package ru.ksart.potatohandbook.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import ru.ksart.potatohandbook.di.PotatoDatabaseCursor
import ru.ksart.potatohandbook.di.PotatoDatabaseRoom
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.db.PotatoDao
import ru.ksart.potatohandbook.model.db.PotatoDatabase
import ru.ksart.potatohandbook.utils.DebugHelper
import javax.inject.Inject

class PotatoRepositoryImpl @Inject constructor(
    @PotatoDatabaseRoom private val roomDb: PotatoDatabase,
    @PotatoDatabaseCursor private val cursorDb: PotatoDatabase,
) : PotatoRepository {
//class PotatoRepositoryImpl @Inject constructor(
//    private val potatoDao: PotatoDao
//): PotatoRepository {

    //    private lateinit var potatoDao: PotatoDao
    private fun getDao(isRoom: Boolean = true): PotatoDao {
        return if (isRoom) roomDb.potatoDao()
        else cursorDb.potatoDao()
    }

    override fun getPotatoAll(): Flow<List<Potato>> {
        return getDao().getPotatoAll().onEach { list ->
            DebugHelper.log("PotatoRepositoryImpl|getPotato list=${list.size}")
        }
    }

    override suspend fun add(item: Potato) {
        getDao().insertPotato(item)
    }

    override suspend fun updatePotato(item: Potato) {
        getDao().updatePotato(item)
    }

    override suspend fun delete(item: Potato) {
        getDao().removePotato(item)
    }

    override suspend fun downloadImage(url: String) : String {

        return ""
    }
}
