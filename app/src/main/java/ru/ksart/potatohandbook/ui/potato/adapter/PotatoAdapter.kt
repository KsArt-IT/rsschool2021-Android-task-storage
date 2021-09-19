package ru.ksart.potatohandbook.ui.potato.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import ru.ksart.potatohandbook.model.db.Potato

class PotatoAdapter(
    private val onClick: (Potato, ImageView) -> Unit
) : ListAdapter<Potato, PotatoViewHolder>(PotatoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PotatoViewHolder {
        return PotatoViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: PotatoViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
