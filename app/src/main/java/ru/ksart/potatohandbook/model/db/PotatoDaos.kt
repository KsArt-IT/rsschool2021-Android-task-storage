package ru.ksart.potatohandbook.model.db

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.utils.DebugHelper

class PotatoDaos(
    private val roomDao: PotatoDatabase,
    private val cursorDao: PotatoDatabase,
) {

    private var _dbmsName = MutableStateFlow<@StringRes Int>(-1)
    val dbmsName = _dbmsName.asStateFlow()

    private var switch = false

    fun setDao(switchDao: Boolean) {
        switch = switchDao
        _dbmsName.value = if (switch) R.string.cursor_dbms_title else R.string.room_dbms_title
    }

    fun getDao(): PotatoDao {
        return if (switch) {
            DebugHelper.log("PotatoDaos|getDao Cursor")
            cursorDao.potatoDao()
        } else {
            DebugHelper.log("PotatoDaos|getDao Room")
            roomDao.potatoDao()
        }
    }
}
