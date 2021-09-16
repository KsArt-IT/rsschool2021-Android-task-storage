package ru.ksart.potatohandbook.model.db.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.db.PotatoContract
import ru.ksart.potatohandbook.model.db.PotatoDao

@Dao
abstract class PotatoRoomDao: PotatoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract override suspend fun insert(potato: Potato): Long

    @Query("SELECT * FROM ${PotatoContract.TABLE_NAME}")
    abstract override fun getAll(): Flow<List<Potato>>

//    @Query("SELECT * FROM ${PotatoContract.TABLE_NAME} WHERE ${PotatoContract.Columns.ID} = :id ORDER BY ${PotatoContract.Columns.NAME} ASC")
//    fun getPotatoId(id: Long): Flow<Potato>

//    @Query("SELECT * FROM ${PotatoContract.TABLE_NAME} WHERE ${PotatoContract.Columns.NAME} LIKE :find+'%' ORDER BY ${PotatoContract.Columns.NAME} ASC")
//    fun getPotatoBySearch(find: String): Flow<Potato>

    @Update
    abstract override suspend fun update(potato: Potato): Int

    @Delete
    abstract override suspend fun remove(potato: Potato)

    @Query("DELETE FROM ${PotatoContract.TABLE_NAME}")
    abstract override suspend fun removeAll()

//    @Query("DELETE FROM ${PotatoContract.TABLE_NAME} WHERE ${PotatoContract.Columns.ID} = :potatoId")
//    suspend fun removePotatoById(potatoId: Long)
}
