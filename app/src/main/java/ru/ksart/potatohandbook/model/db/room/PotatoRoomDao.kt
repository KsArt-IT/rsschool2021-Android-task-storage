package ru.ksart.potatohandbook.model.db.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.db.PotatoContract
import ru.ksart.potatohandbook.model.db.PotatoDao

@Dao
abstract class PotatoRoomDao: PotatoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun insertPotato(potato: Potato): Long

    @Query("SELECT * FROM ${PotatoContract.TABLE_NAME} ORDER BY ${PotatoContract.Columns.NAME} ASC")
    abstract override fun getPotatoAll(): Flow<List<Potato>>

//    @Query("SELECT * FROM ${PotatoContract.TABLE_NAME} WHERE ${PotatoContract.Columns.ID} = :id ORDER BY ${PotatoContract.Columns.NAME} ASC")
//    fun getPotatoId(id: Long): Flow<Potato>

//    @Query("SELECT * FROM ${PotatoContract.TABLE_NAME} WHERE ${PotatoContract.Columns.NAME} LIKE :find+'%' ORDER BY ${PotatoContract.Columns.NAME} ASC")
//    fun getPotatoBySearch(find: String): Flow<Potato>

    @Update
    abstract override suspend fun updatePotato(potato: Potato)

    @Delete
    abstract override suspend fun removePotato(potato: Potato)

//    @Query("DELETE FROM ${PotatoContract.TABLE_NAME} WHERE ${PotatoContract.Columns.ID} = :potatoId")
//    suspend fun removePotatoById(potatoId: Long)
}
