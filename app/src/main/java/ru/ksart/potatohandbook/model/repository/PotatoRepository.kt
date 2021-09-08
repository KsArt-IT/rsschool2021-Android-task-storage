package ru.ksart.potatohandbook.model.repository

import kotlinx.coroutines.flow.Flow
import ru.ksart.potatohandbook.model.data.PeriodRipening
import ru.ksart.potatohandbook.model.data.PotatoVariety
import ru.ksart.potatohandbook.model.data.Productivity
import ru.ksart.potatohandbook.model.db.Potato

interface PotatoRepository {
    suspend fun initData()

    suspend fun readFilter(): Pair<Boolean, Triple<PotatoVariety?, PeriodRipening?, Productivity?>>

    fun getPotatoAll(): Flow<List<Potato>>

    suspend fun add(item: Potato)
    suspend fun updatePotato(item: Potato)

    suspend fun delete(item: Potato)
    suspend fun deleteAll()

    suspend fun downloadImage(name: String, url: String) : String
}
