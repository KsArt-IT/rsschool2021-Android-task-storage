package ru.ksart.potatohandbook.ui.potato.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.databinding.FragmentPotatoAddBinding
import ru.ksart.potatohandbook.model.data.PeriodRipening
import ru.ksart.potatohandbook.model.data.PotatoVariety
import ru.ksart.potatohandbook.model.data.Productivity
import ru.ksart.potatohandbook.ui.ShowMenu
import ru.ksart.potatohandbook.ui.extensions.hideKeyboardFrom
import ru.ksart.potatohandbook.ui.extensions.setAdapterFromList
import ru.ksart.potatohandbook.ui.extensions.setItemByIndex
import ru.ksart.potatohandbook.ui.extensions.toast
import ru.ksart.potatohandbook.utils.DebugHelper

@AndroidEntryPoint
class PotatoAddFragment : Fragment() {

    private var binding: FragmentPotatoAddBinding? = null
    private val viewModel by viewModels<PotatoAddViewModel>()
    private val parent get() = activity?.let { it as? ShowMenu }

    private val args: PotatoAddFragmentArgs by navArgs()
    private val item by lazy { args.item }

    private var potatoVariety = 0
    private var periodRipening = 0
    private var potatoProductivity = 0
/*
    private var potatoVariety: PotatoVariety = PotatoVariety.Na
    private var periodRipening: PeriodRipening = PeriodRipening.Na
    private var potatoProductivity: Productivity = Productivity.Na
*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPotatoAddBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DebugHelper.log("PotatoAddFragment|onViewCreated ${this.hashCode()}")
        parent?.showMenu(show = false)
        initListener()
        initAdapters()
        bindViewModel()
        // если это редактирование
        item?.let { potato ->
            views {
                name.editText?.setText(potato.name)
                description.editText?.setText(potato.description)
                imageUrl.editText?.setText(potato.imageUrl)
                potatoVariety = potato.variety.ordinal
                (variety.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(potatoVariety)
                periodRipening = potato.ripening.ordinal
                (ripening.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(periodRipening)
                potatoProductivity = potato.productivity.ordinal
                (productivity.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(
                    potatoProductivity
                )
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun initAdapters() {
        views {
            (variety.editText as? MaterialAutoCompleteTextView)?.setAdapterFromList(
                PotatoVariety.values().map {
                    getString(it.caption)
                }
            )
            (variety.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(0)

            (ripening.editText as? MaterialAutoCompleteTextView)?.setAdapterFromList(
                PeriodRipening.values().map {
                    getString(it.caption)
                }
            )
            (ripening.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(0)

            (productivity.editText as? MaterialAutoCompleteTextView)?.setAdapterFromList(
                Productivity.values().map {
                    getString(it.caption)
                }
            )
            (productivity.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(0)
        }
    }

    private fun initListener() {
        views {
            addPotatoButton.setOnClickListener { insertPotato() }
            name.editText?.run {
                doAfterTextChanged {
                    it?.toString()?.let { value ->
                        viewModel.checkNameField(value)
                    }
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus.not()) viewModel.checkNameField(text.toString())
                }
            }
            description.editText?.run {
                doAfterTextChanged {
                    it?.toString()?.let { value ->
                        viewModel.checkDescriptionField(value)
                    }
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus.not()) viewModel.checkDescriptionField(text.toString())
                }
            }

            variety.editText?.setOnFocusChangeListener { v, _ -> context?.hideKeyboardFrom(v) }
            ripening.editText?.setOnFocusChangeListener { v, _ -> context?.hideKeyboardFrom(v) }
            productivity.editText?.setOnFocusChangeListener { v, _ -> context?.hideKeyboardFrom(v) }

            // следим за изменением
            (variety.editText as? MaterialAutoCompleteTextView)
                ?.setOnItemClickListener { _, _, position, _ -> potatoVariety = position }
            (ripening.editText as? MaterialAutoCompleteTextView)
                ?.setOnItemClickListener { _, _, position, _ -> periodRipening = position }
            (productivity.editText as? MaterialAutoCompleteTextView)
                ?.setOnItemClickListener { _, _, position, _ -> potatoProductivity = position }
        }
    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.isItemAdded.collect {
                if (it == -1) return@collect
                close(it == 1)
            }
        }
        lifecycleScope.launchWhenStarted { viewModel.isAddButtonEnabled.collect(::activateAddButton) }
        lifecycleScope.launchWhenStarted { viewModel.isToast.collect { if (it.isNotBlank()) toast(it) } }
        lifecycleScope.launchWhenStarted {
            viewModel.isNameFieldError.collect { isError ->
                views {
                    name.editText?.error =
                        if (isError) getString(R.string.error_name_text) else null
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isDescriptionFieldError.collect { isError ->
                views {
                    description.editText?.error =
                        if (isError) getString(R.string.error_description_text) else null
                }
            }
        }
    }

    private fun insertPotato() {
        views {
            switchEnabledFields(false)
            // добавление или редактирование
            viewModel.add(
                id = item?.id ?: 0,
                name = name.editText?.text.toString(),
                description = description.editText?.text.toString(),
                imageUrl = imageUrl.editText?.text.toString(),
                variety = potatoVariety,
                ripening = periodRipening,
                productivity = potatoProductivity,
            )
        }
    }

    private fun switchEnabledFields(enable: Boolean) {
        views {
            addPotatoButton.isEnabled = enable
            name.isEnabled = enable
            description.isEnabled = enable
            imageUrl.isEnabled = enable
            variety.isEnabled = enable
            ripening.isEnabled = enable
            productivity.isEnabled = enable
        }
    }

    private fun activateAddButton(enable: Boolean) {
        views {
            addPotatoButton.isEnabled = enable
        }
    }

    private fun close(close: Boolean) {
        DebugHelper.log("PotatoAddFragment|close $close")
        if (close) {
            toast("Added!")
            findNavController().navigateUp()
        } else {
            toast("Error adding element!")
            switchEnabledFields(true)
        }
    }

    private fun <T> views(block: FragmentPotatoAddBinding.() -> T): T? = binding?.block()
}
