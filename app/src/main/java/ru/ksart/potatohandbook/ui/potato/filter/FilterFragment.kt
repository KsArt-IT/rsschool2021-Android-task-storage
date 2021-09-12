package ru.ksart.potatohandbook.ui.potato.filter

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.ui.ShowMenu
import ru.ksart.potatohandbook.utils.DebugHelper

class FilterFragment : PreferenceFragmentCompat() {
    private val parent get() = activity?.let { it as? ShowMenu }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        DebugHelper.log("FilterFragment|onCreatePreferences ${this.hashCode()}")
        parent?.showMenu(show = false)
        addPreferencesFromResource(R.xml.fragment_filter)
    }
}