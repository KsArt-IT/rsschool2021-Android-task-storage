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
import ru.ksart.potatohandbook.model.db.Potato
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

    private var potato: Potato? = null

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
        savedInstanceState ?: item?.let { viewModel.savePotato(it) }
        // прочитаем состояние
        potato = viewModel.getPotato()
        // начальные значения
        views {
            addPotatoButton.setText(if (potato?.id == 0L) R.string.add_button_text else R.string.edit_button_text)
            name.editText?.setText(potato?.name ?: "")
            description.editText?.setText(potato?.description ?: "")
            imageUrl.editText?.setText(potato?.imageUrl ?: "")
            (variety.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(
                potato?.variety?.ordinal ?: 0
            )
            (ripening.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(
                potato?.ripening?.ordinal ?: 0
            )
            (productivity.editText as? MaterialAutoCompleteTextView)?.setItemByIndex(
                potato?.productivity?.ordinal ?: 0
            )
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
                        viewModel.saveName(value)
                    }
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus.not()) viewModel.checkNameField(text.toString())
                }
            }
            description.editText?.run {
                doAfterTextChanged {
                    it?.toString()?.let { value ->
                        viewModel.saveDescription(value)
                    }
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus.not()) viewModel.checkDescriptionField(text.toString())
                }
            }

            imageUrl.editText?.doAfterTextChanged {
                it?.toString()?.let { value ->
                    viewModel.saveImageUrl(value)
                }
            }

            variety.editText?.setOnFocusChangeListener { v, _ -> context?.hideKeyboardFrom(v) }
            ripening.editText?.setOnFocusChangeListener { v, _ -> context?.hideKeyboardFrom(v) }
            productivity.editText?.setOnFocusChangeListener { v, _ -> context?.hideKeyboardFrom(v) }

            // следим за изменением
            (variety.editText as? MaterialAutoCompleteTextView)
                ?.setOnItemClickListener { _, _, position, _ ->
                    viewModel.saveVariety(position)
                }
            (ripening.editText as? MaterialAutoCompleteTextView)
                ?.setOnItemClickListener { _, _, position, _ ->
                    viewModel.saveRipening(position)
                }
            (productivity.editText as? MaterialAutoCompleteTextView)
                ?.setOnItemClickListener { _, _, position, _ ->
                    viewModel.saveProductivity(position)
                }
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
            viewModel.add()
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
            toast(R.string.complete_text)
            findNavController().navigateUp()
        } else {
            toast(R.string.error_added_item)
            switchEnabledFields(true)
        }
    }

    private fun <T> views(block: FragmentPotatoAddBinding.() -> T): T? = binding?.block()
}
