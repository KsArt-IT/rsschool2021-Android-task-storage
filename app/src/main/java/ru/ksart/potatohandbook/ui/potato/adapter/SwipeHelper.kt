package ru.ksart.potatohandbook.ui.potato.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import ru.ksart.potatohandbook.model.db.Potato

class SwipeHelper(onSwiped: (Potato) -> Unit): ItemTouchHelper(SwipeCallback(onSwiped))
