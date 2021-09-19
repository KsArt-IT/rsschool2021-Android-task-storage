package ru.ksart.potatohandbook.model.repository

import kotlinx.coroutines.flow.Flow
import ru.ksart.potatohandbook.model.data.PotatoState
import ru.ksart.potatohandbook.model.db.Potato

interface PotatoRepository {
    val dbmsName: Flow<Int>
    val changeFilter: Flow<Int>

    suspend fun initData()

    suspend fun registerChangeFilter()
    suspend fun unregisterChangeFilter()

    suspend fun readFilter(): PotatoState

    fun getAll(): Flow<List<Potato>>

    suspend fun add(item: Potato): Long
    suspend fun update(item: Potato): Int

    suspend fun delete(item: Potato)
    suspend fun deleteAll()

    suspend fun downloadImage(name: String, url: String): String?
}
