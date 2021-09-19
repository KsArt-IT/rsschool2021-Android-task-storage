package ru.ksart.potatohandbook.model.db.cursor

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.ksart.potatohandbook.model.db.PotatoContract
import ru.ksart.potatohandbook.model.db.PotatoDao
import ru.ksart.potatohandbook.model.db.PotatoDatabase
import ru.ksart.potatohandbook.model.db.PotatoDatabaseVersion
import ru.ksart.potatohandbook.utils.DebugHelper

class PotatoDatabaseCursorImpl(context: Context) :
    SQLiteOpenHelper(
        context,
        PotatoDatabaseVersion.DB_NAME,
        null,
        PotatoDatabaseVersion.DB_VERSION
    ),
    PotatoDatabase {
    private var db: SQLiteDatabase? = null
    private var dao: PotatoCursorDao? = null

    init {
        try {
            db = writableDatabase
            dao = db?.let { PotatoCursorDao(it) }
        } catch (e: SQLException) {
            try {
                db?.close()
                db = writableDatabase
                dao = db?.let { PotatoCursorDao(it) }
            } catch (e: SQLException) {
                DebugHelper.log("PotatoDatabaseCursorImpl|init error", e)
            }
        }
    }

    override fun potatoDao(): PotatoDao = requireNotNull(dao) { "Error! db not initialized" }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            DebugHelper.log("PotatoDatabaseCursorImpl|onCreate")
            db.execSQL(CREATE_TABLE_SQL)
            db.execSQL(CREATE_INDEX_NAME_SQL)
        } catch (exception: SQLException) {
            DebugHelper.log(
                "PotatoDatabaseCursorImpl|onCreate error: Exception while trying to create database",
                exception
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            DebugHelper.log("PotatoDatabaseCursorImpl|onUpgrade")
            db.execSQL(DELETE_INDEX_NAME_SQL)
            db.execSQL(DELETE_TABLE_SQL)
            onCreate(db)
        } catch (exception: SQLException) {
            DebugHelper.log(
                "PotatoDatabaseCursorImpl|onUpgrade error: Exception while trying to Upgrade database",
                exception
            )
        }
    }

    private companion object {
        private const val CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS ${PotatoContract.TABLE_NAME} (" +
                "${PotatoContract.Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "${PotatoContract.Columns.NAME} TEXT NOT NULL," +
                "${PotatoContract.Columns.DESCRIPTION} TEXT NOT NULL," +
                "${PotatoContract.Columns.IMAGE_URI} TEXT," +
                "${PotatoContract.Columns.IMAGE_URL} TEXT," +
                "${PotatoContract.Columns.VARIETY} TEXT NOT NULL," +
                "${PotatoContract.Columns.RIPENING} TEXT NOT NULL," +
                "${PotatoContract.Columns.PRODUCTIVITY} TEXT NOT NULL" +
                ");"
        private const val CREATE_INDEX_NAME_SQL =
            "CREATE UNIQUE INDEX IF NOT EXISTS index_${PotatoContract.TABLE_NAME}_${PotatoContract.Columns.NAME} ON " +
                "${PotatoContract.TABLE_NAME} (${PotatoContract.Columns.NAME});"

        private const val DELETE_TABLE_SQL = "DROP TABLE IF EXISTS ${PotatoContract.TABLE_NAME}"
        private const val DELETE_INDEX_NAME_SQL = "DROP INDEX IF EXISTS " +
            "index_${PotatoContract.TABLE_NAME}_${PotatoContract.Columns.NAME}"
    }
}
