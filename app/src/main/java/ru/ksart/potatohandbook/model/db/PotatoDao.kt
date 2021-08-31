package ru.ksart.potatohandbook.model.db

import kotlinx.coroutines.flow.Flow

interface PotatoDao {
    fun getPotatoAll(): Flow<List<Potato>>
    suspend fun insertPotato(potato: Potato): Long
    suspend fun updatePotato(potato: Potato)
    suspend fun removePotato(potato: Potato)
}