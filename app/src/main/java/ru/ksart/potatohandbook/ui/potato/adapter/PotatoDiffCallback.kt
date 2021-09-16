package ru.ksart.potatohandbook.ui.potato.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.ksart.potatohandbook.model.db.Potato

class PotatoDiffCallback : DiffUtil.ItemCallback<Potato>() {

    override fun areItemsTheSame(oldItem: Potato, newItem: Potato): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Potato, newItem: Potato): Boolean {
        return oldItem == newItem
    }
}
