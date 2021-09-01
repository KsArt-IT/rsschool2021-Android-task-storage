package ru.ksart.potatohandbook.model.repository

import kotlinx.coroutines.flow.Flow
import ru.ksart.potatohandbook.model.db.Potato

interface PotatoRepository {
    fun getPotatoAll(): Flow<List<Potato>>

    suspend fun delete(item: Potato)
    suspend fun add(item: Potato)
    suspend fun updatePotato(item: Potato)

    suspend fun downloadImage(url: String) : String
}
