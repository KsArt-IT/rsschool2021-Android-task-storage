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
    val dbmsName get() = _dbmsName.asStateFlow()

    fun getDao(switch: Boolean): PotatoDao {
        return if (switch) {
            DebugHelper.log("PotatoDaos|getDao Cursor")
            _dbmsName.value = R.string.cursor_dbms_title
            cursorDao.potatoDao()
        } else {
            DebugHelper.log("PotatoDaos|getDao Room")
            _dbmsName.value = R.string.room_dbms_title
            roomDao.potatoDao()
        }
    }
}