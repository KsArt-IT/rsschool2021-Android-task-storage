package ru.ksart.potatohandbook.ui.potato.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.databinding.ItemPotatoBinding
import ru.ksart.potatohandbook.model.db.Potato
import java.io.File

class PotatoViewHolder(
    private val binding: ItemPotatoBinding,
    private val onClick: (Potato) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    var item: Potato? = null
        private set

    init {
        binding.root.setOnClickListener {
            item?.let(onClick)
        }
    }

    fun onBind(item: Potato) {
        this.item = item

        views {
            caption.text = item.name
            description.text = item.description
            val imageShow = if (item.imageUri != null && File(item.imageUri).exists()) item.imageUri
            else item.imageUrl
            imageShow?.let {
                image.load(imageShow) {
                    crossfade(true)
                    placeholder(R.drawable.ic_download)
                    error(R.drawable.ic_error)
                    transformations(CircleCropTransformation())
                }
            } ?: image.load(R.drawable.potato)
        }
    }

    private fun <T> views(block: ItemPotatoBinding.() -> T): T? = binding.block()

    companion object {
        fun create(
            parent: ViewGroup,
            onClick: (Potato) -> Unit
        ) = ItemPotatoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let { PotatoViewHolder(it, onClick) }
    }
}
