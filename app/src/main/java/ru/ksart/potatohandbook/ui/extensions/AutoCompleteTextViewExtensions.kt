package ru.ksart.potatohandbook.ui.extensions

import android.widget.ArrayAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import ru.ksart.potatohandbook.R

fun <T> MaterialAutoCompleteTextView.setAdapterFromList(list: List<T>) {
    val adapter = ArrayAdapter(this.context, R.layout.menu_exposed_dropdown_item, list)
    setAdapter(adapter)
}

fun MaterialAutoCompleteTextView.setItemByIndex(index: Int) {
//    DebugHelper.log("setItemByIndex index=$index, size=${adapter.count}")
    setText(if (index in 0 until adapter.count) adapter.getItem(index).toString() else "", false)
}
