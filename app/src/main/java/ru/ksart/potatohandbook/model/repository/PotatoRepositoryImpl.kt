package ru.ksart.potatohandbook.model.repository

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.db.PotatoDao
import ru.ksart.potatohandbook.model.network.Api
import ru.ksart.potatohandbook.utils.DebugHelper
import java.io.File
import javax.inject.Inject

class PotatoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: PotatoDao,
    private val api: Api,
) : PotatoRepository {

    override fun getPotatoAll(): Flow<List<Potato>> {
        return dao.getPotatoAll().onEach { list ->
            DebugHelper.log("PotatoRepositoryImpl|getPotato list=${list.size}")
        }
    }

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

    override suspend fun downloadImage(name: String, url: String): String {
        return saveImage(name, url)
    }

    //----------------------

    private suspend fun saveImage(name: String, url: String): String = withContext(Dispatchers.IO) {
        if (url.isBlank()) return@withContext ""
        var file: File? = null
        try {
            val folder = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                DebugHelper.log("PotatoRepositoryImpl|saveImage ExternalStorage")
                context.getExternalFilesDir(POTATO_IMAGE_FILES_PATH)
            } else {
                DebugHelper.log("PotatoRepositoryImpl|saveImage InternalStorage")
                File(context.filesDir.path+"/$POTATO_IMAGE_FILES_PATH")
            }
            val ext = File(url).extension
            file = File(folder, "$name.$ext")
            DebugHelper.log("PotatoRepositoryImpl|saveImage file=$file")
            downloadFile(url, file)
            file.toUri().toString()
        } catch (e: Exception) {
            DebugHelper.log("PotatoRepositoryImpl|saveImage error: ${e.localizedMessage}")
            file?.takeIf { it.exists() }?.delete()
            ""
        }
    }

    private suspend fun downloadFile(url: String, file: File) {
        withContext(Dispatchers.IO) {
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
            DebugHelper.log("PotatoRepositoryImpl|deleteMediaUri file=$fileName")
            try {
                val file = File(fileName)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                DebugHelper.log("PotatoRepositoryImpl|deleteMediaUri error")
            }
        }
    }

    companion object {
        private const val POTATO_IMAGE_FILES_PATH = "potato_images"
    }
}
