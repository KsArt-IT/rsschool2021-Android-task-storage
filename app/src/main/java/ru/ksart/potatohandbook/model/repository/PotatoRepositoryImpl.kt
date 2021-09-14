package ru.ksart.potatohandbook.model.repository

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.model.data.*
import ru.ksart.potatohandbook.model.db.*
import ru.ksart.potatohandbook.model.network.Api
import ru.ksart.potatohandbook.utils.DebugHelper
import java.io.File
import javax.inject.Inject
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener


class PotatoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val daos: PotatoDaos,
    private val api: Api,
) : PotatoRepository {

    private val res = context.resources

    private val dao: PotatoDao get() = daos.getDao()
    override val dbmsName: StateFlow<Int> get() = daos.dbmsName

    private val _changeFilter = MutableStateFlow(-1)
    override val changeFilter get() = _changeFilter.asStateFlow()

    private val defaultPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private val listener by lazy {
        OnSharedPreferenceChangeListener { prefs, key ->
            _changeFilter.value++
            DebugHelper.log("PotatoRepositoryImpl|registerChangeFilter listener ${_changeFilter.value} key=$key")
        }
    }

    // ключи для чтения параметров sharedPref
    private val firstStartKey: String by lazy { res.getString(R.string.first_start_key) }
    private val dbmsKey: String by lazy { res.getString(R.string.dbms_switch_key) }
    private val nameKey: String by lazy { res.getString(R.string.name_switch_key) }
    private val varietyKey: String by lazy { res.getString(R.string.variety_key) }
    private val ripeningKey: String by lazy { res.getString(R.string.ripening_key) }
    private val productivityKey: String by lazy { res.getString(R.string.productivity_key) }

    override suspend fun registerChangeFilter() {
        withContext(Dispatchers.IO) {
            startFirst()
            DebugHelper.log("PotatoRepositoryImpl|registerChangeFilter init")
            defaultPreferences.registerOnSharedPreferenceChangeListener(listener)
        }
    }

    override suspend fun unregisterChangeFilter() {
        withContext(Dispatchers.IO) {
            defaultPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    private suspend fun startFirst() {
        withContext(Dispatchers.IO) {
            val firstStart = defaultPreferences.getBoolean(firstStartKey, true)
            // если первый запуск
            if (firstStart) {
                defaultPreferences.edit().putBoolean(firstStartKey, false).apply()
                initData()
            }
        }
    }

    override suspend fun initData() {
        withContext(Dispatchers.IO) {
            DebugHelper.log("PotatoRepositoryImpl|initData")
            val list = PotatoDatabaseInitial().potatoInitData
            list.forEach { potato ->
                val fileUri = potato.imageUrl?.let { downloadImage(potato.name, it) }
                add(potato.copy(imageUri = fileUri))
            }
        }
    }

    override suspend fun readFilter(): PotatoState = withContext(Dispatchers.IO) {
        val daoSwitch = defaultPreferences.getBoolean(dbmsKey, res.getBoolean(R.bool.dbms_switch_value))
        DebugHelper.log("PotatoRepositoryImpl|readFilter daoSwitch=$daoSwitch")
        daos.setDao(daoSwitch)
        val name = defaultPreferences.getBoolean(nameKey, res.getBoolean(R.bool.name_switch_value))
        val variety =
            defaultPreferences.getString(varietyKey, "0")?.toIntOrNull() ?: 0
        val ripening =
            defaultPreferences.getString(ripeningKey, "0")?.toIntOrNull() ?: 0
        val productivity =
            defaultPreferences.getString(productivityKey, "0")?.toIntOrNull() ?: 0
        PotatoState(
            daoSwitch,
            name,
            PotatoFilter(
                if (variety in 1..PotatoVariety.values().lastIndex) PotatoVariety.values()[variety] else null,
                if (ripening in 1..PeriodRipening.values().lastIndex) PeriodRipening.values()[ripening] else null,
                if (productivity in 1..Productivity.values().lastIndex) Productivity.values()[productivity] else null
            )
        )
    }

    //----------------------

    override fun getPotatoAll(): Flow<List<Potato>> = dao.getPotatoAll()

    override suspend fun add(item: Potato) {
        dao.insertPotato(item)
    }

    override suspend fun updatePotato(item: Potato) {
        dao.updatePotato(item)
    }

    override suspend fun delete(item: Potato) {
        dao.removePotato(item)
        item.imageUri?.let { deleteFile(it) }
    }

    override suspend fun deleteAll() {
        dao.removePotatoAll()
        deleteFolder(POTATO_IMAGE_FILES_PATH)
    }

    //----------------------
    override suspend fun downloadImage(name: String, url: String): String? {
        return saveImage(name, url)
    }

    private suspend fun saveImage(name: String, url: String): String? =
        withContext(Dispatchers.IO) {
            if (url.isBlank()) return@withContext null
            var file: File? = null
            try {
                val folder =
                    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                        context.getExternalFilesDir(POTATO_IMAGE_FILES_PATH).also {
                            DebugHelper.log("PotatoRepositoryImpl|saveImage ExternalStorage folder=$it")
                        }
                    } else {
                        File(context.filesDir.path.plus(POTATO_IMAGE_FILES_PATH)).also {
                            DebugHelper.log("PotatoRepositoryImpl|saveImage InternalStorage folder=$it")
                        }
                    } ?: return@withContext null
                val ext = File(url).extension
                file = File(folder, "$name.$ext")
                DebugHelper.log("PotatoRepositoryImpl|saveImage file=$file")
                downloadFile(url, file)
                file.toUri().toString()
            } catch (e: Exception) {
                DebugHelper.log("PotatoRepositoryImpl|saveImage error: ${e.localizedMessage}")
                try {
                    file?.takeIf { it.exists() }?.delete()
                } catch (e: Exception) {
                }
                null
            }
        }

    private suspend fun downloadFile(url: String, file: File) {
        withContext(Dispatchers.IO) {
            DebugHelper.log("PotatoRepositoryImpl|downloadFile file=$url")
            file.outputStream().use { fileOutputStream ->
                api.getFile(url)
                    .byteStream()
                    .use { inputStream ->
                        inputStream.copyTo(fileOutputStream)
                    }
            }
        }
    }

    private suspend fun deleteFile(fileName: String) {
        withContext(Dispatchers.IO) {
            DebugHelper.log("PotatoRepositoryImpl|deleteFile file=$fileName")
            try {
                val file = File(fileName)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                DebugHelper.log("PotatoRepositoryImpl|deleteFile error")
            }
        }
    }

    private suspend fun deleteFolder(folderName: String) {
        withContext(Dispatchers.IO) {
            DebugHelper.log("PotatoRepositoryImpl|deleteFolder file=$folderName")
            try {
                var folder =
                    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                        context.getExternalFilesDir(folderName)
                    } else null
                folder?.takeIf { it.exists() }?.delete()
                folder = File(context.filesDir.path.plus(folderName))
                if (folder.exists()) folder.delete()
            } catch (e: Exception) {
                DebugHelper.log("PotatoRepositoryImpl|deleteFolder error")
            }
        }
    }

    companion object {
        private const val POTATO_IMAGE_FILES_PATH = "potato_images"
    }
}
