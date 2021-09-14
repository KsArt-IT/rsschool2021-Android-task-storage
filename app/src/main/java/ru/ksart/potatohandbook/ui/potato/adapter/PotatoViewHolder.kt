package ru.ksart.potatohandbook.ui.potato.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.databinding.ItemPotatoBinding
import ru.ksart.potatohandbook.model.db.Potato
import java.io.File

class PotatoViewHolder(
    private val binding: ItemPotatoBinding,
    private val onClick: (Potato, ImageView) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    var item: Potato? = null
        private set

    init {
        binding.run { root.setOnClickListener { item?.let { onClick(it, image) } } }
    }

    fun onBind(item: Potato) {
        this.item = item

        binding.run {
            caption.text = item.name
            description.text = item.description
            val imageShow = item.imageUri?.takeIf { it.isNotBlank() && File(it).exists() }
                ?: item.imageUrl
            image.apply { transitionName = item.id.toString() }
                .load(imageShow ?: "-") {
                    crossfade(true)
                    placeholder(R.drawable.ic_download)
                    error(R.drawable.potato)
                    transformations(CircleCropTransformation())
                    build()
                }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClick: (Potato, ImageView) -> Unit
        ) = ItemPotatoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let { PotatoViewHolder(it, onClick) }
    }
}
