package ru.ksart.potatohandbook.model.db.cursor

import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.ksart.potatohandbook.model.db.EnumConverter
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.db.PotatoContract
import ru.ksart.potatohandbook.model.db.PotatoDao
import ru.ksart.potatohandbook.utils.DebugHelper

class PotatoCursorDao(private val db: SQLiteDatabase) : PotatoDao {

    private val converter = EnumConverter()
    private val changeFlow = MutableStateFlow<Long>(-1)

    override fun getAll(): Flow<List<Potato>> = changeFlow.map {
        updateList()
    }.onEach {
        DebugHelper.log("PotatoCursorDao|getPotatoAll list=${it.size} на потоке ${Thread.currentThread().name}")
    }

    private fun updateList(): List<Potato> {
        DebugHelper.log("PotatoCursorDao|updateList in")
        // получим курсор
        val cursor = getCursor()
        DebugHelper.log("PotatoCursorDao|updateList cursor=${cursor?.count}")
        // прочитаем записи из курсора
        return cursor?.takeIf { it.count > 0 }?.let { readFromCursor(it) } ?: emptyList()
    }

    private fun getCursor(): Cursor? {
        return db.query(
            PotatoContract.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    private fun readFromCursor(cursor: Cursor): List<Potato> {
        val list = mutableListOf<Potato>()
        cursor.use {
            // получим индексы полей для чтения
            val indexOfId = it.getColumnIndexOrThrow(PotatoContract.Columns.ID)
            val indexOfName = it.getColumnIndexOrThrow(PotatoContract.Columns.NAME)
            val indexOfDescription = it.getColumnIndexOrThrow(PotatoContract.Columns.DESCRIPTION)
            val indexOfImageUri = it.getColumnIndexOrThrow(PotatoContract.Columns.IMAGE_URI)
            val indexOfImageUrl = it.getColumnIndexOrThrow(PotatoContract.Columns.IMAGE_URL)
            val indexOfVariety = it.getColumnIndexOrThrow(PotatoContract.Columns.VARIETY)
            val indexOfRipening = it.getColumnIndexOrThrow(PotatoContract.Columns.RIPENING)
            val indexOfProductivity = it.getColumnIndexOrThrow(PotatoContract.Columns.PRODUCTIVITY)
            // читаем все записи
            while (it.moveToNext()) {
                val item = Potato(
                    id = it.getLong(indexOfId),
                    name = if (it.isNull(indexOfName)) "" else it.getString(indexOfName),
                    description = if (it.isNull(indexOfDescription)) "" else it.getString(
                        indexOfDescription
                    ),
                    imageUri = if (it.isNull(indexOfImageUri)) null else it.getString(
                        indexOfImageUri
                    ),
                    imageUrl = if (it.isNull(indexOfImageUrl)) null else it.getString(
                        indexOfImageUrl
                    ),
                    variety = converter.converterStringToPotatoVariety(
                        if (it.isNull(indexOfVariety)) "" else it.getString(
                            indexOfVariety
                        )
                    ),
                    ripening = converter.converterStringToPeriodRipening(
                        if (it.isNull(indexOfRipening)) "" else it.getString(
                            indexOfRipening
                        )
                    ),
                    productivity = converter.converterStringToProductivity(
                        if (it.isNull(indexOfProductivity)) "" else it.getString(
                            indexOfProductivity
                        )
                    )
                )
                list.add(item)
            }
        }
        DebugHelper.log("PotatoCursorDao|updateList list=${list.size}")
        return list.toList()
    }

    override suspend fun insert(potato: Potato): Long = withContext(Dispatchers.IO) {
        try {
            val values = getValues(potato)
            // удалим id
            if (values.containsKey(PotatoContract.Columns.ID)) values.remove(PotatoContract.Columns.ID)
            db.beginTransaction()
            val rowId = db.insert(PotatoContract.TABLE_NAME, null, values)
            db.setTransactionSuccessful()
            DebugHelper.log("PotatoCursorDao|insertPotato id=${potato.id} - $rowId name=${potato.name}")
            rowId
        } catch (e: SQLException) {
            DebugHelper.log("PotatoCursorDao|insertPotato error insert name=${potato.name}", e)
            throw e
        } finally {
            db.endTransaction()
            changeFlow.value++
        }
    }

    override suspend fun update(potato: Potato): Int = withContext(Dispatchers.IO) {
        try {
            DebugHelper.log("PotatoCursorDao|updatePotato update in")
            val values = getValues(potato)
            db.beginTransaction()
            val count = db.update(
                PotatoContract.TABLE_NAME,
                values,
                "${PotatoContract.Columns.ID} = ?",
                arrayOf(potato.id.toString())
            )
            db.setTransactionSuccessful()
            DebugHelper.log("PotatoCursorDao|updatePotato update count=$count id=${potato.id}")
            count
        } catch (e: SQLException) {
            DebugHelper.log(
                "PotatoCursorDao|updatePotato error update id=${potato.id} name=${potato.name}",
                e
            )
            throw e
        } finally {
            db.endTransaction()
            changeFlow.value++
        }
    }

    private fun getValues(potato: Potato): ContentValues {
        return ContentValues().apply {
            put(PotatoContract.Columns.ID, potato.id)
            put(PotatoContract.Columns.NAME, potato.name)
            put(PotatoContract.Columns.DESCRIPTION, potato.description)
            put(PotatoContract.Columns.IMAGE_URI, potato.imageUri)
            put(PotatoContract.Columns.IMAGE_URL, potato.imageUrl)
            put(
                PotatoContract.Columns.VARIETY,
                converter.converterPotatoVarietyToString(potato.variety)
            )
            put(
                PotatoContract.Columns.RIPENING,
                converter.converterPeriodRipeningToString(potato.ripening)
            )
            put(
                PotatoContract.Columns.PRODUCTIVITY,
                converter.converterProductivityToString(potato.productivity)
            )
        }
    }

    override suspend fun remove(potato: Potato) {
        withContext(Dispatchers.IO) {
            try {
                db.beginTransaction()
                val count = db.delete(
                    PotatoContract.TABLE_NAME,
                    "${PotatoContract.Columns.ID} = ?",
                    arrayOf(potato.id.toString())
                )
                db.setTransactionSuccessful()
                DebugHelper.log("PotatoCursorDao|insertPotato deleted count=$count")
            } catch (e: SQLException) {
                DebugHelper.log(
                    "PotatoCursorDao|insertPotato error delete name=${potato.name}",
                    e
                )
            } finally {
                db.endTransaction()
                changeFlow.value++
            }
        }
    }

    override suspend fun removeAll() {
        withContext(Dispatchers.IO) {
            try {
                db.beginTransaction()
                val count = db.delete(PotatoContract.TABLE_NAME, null, null)
                db.setTransactionSuccessful()
                DebugHelper.log("PotatoCursorDao|insertPotato deleted count=$count")
            } catch (e: SQLException) {
                DebugHelper.log("PotatoCursorDao|insertPotato error delete all records", e)
            } finally {
                db.endTransaction()
                changeFlow.value++
            }
        }
    }
}
