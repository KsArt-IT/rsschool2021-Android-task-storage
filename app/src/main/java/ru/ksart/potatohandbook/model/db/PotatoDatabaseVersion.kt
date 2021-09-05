package ru.ksart.potatohandbook.model.db

object PotatoDatabaseVersion {
    const val DB_VERSION = 1
    const val DB_NAME = "potato_database.db"

    val useDbMs: DbMs get() = DbMs.values()[useDbMsIndex]
    private var useDbMsIndex: Int = 0

    fun switch() {
        useDbMsIndex++
        if (useDbMsIndex >= DbMs.values().size) useDbMsIndex = 0
    }
}