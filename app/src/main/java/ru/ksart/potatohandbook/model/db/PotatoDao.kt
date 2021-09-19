package ru.ksart.potatohandbook.model.db

import kotlinx.coroutines.flow.Flow

interface PotatoDao {
    fun getAll(): Flow<List<Potato>>
    suspend fun insert(potato: Potato): Long
    suspend fun update(potato: Potato): Int
    suspend fun remove(potato: Potato)
    suspend fun removeAll()
}
