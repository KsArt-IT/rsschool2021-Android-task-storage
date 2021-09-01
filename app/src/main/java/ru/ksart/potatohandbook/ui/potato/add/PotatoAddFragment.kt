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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.ksart.potatohandbook.databinding.FragmentPotatoAddBinding
import ru.ksart.potatohandbook.ui.extensions.toast
import ru.ksart.potatohandbook.utils.DebugHelper

@AndroidEntryPoint
class PotatoAddFragment : Fragment() {

    private var binding: FragmentPotatoAddBinding? = null
    private val viewModel by viewModels<PotatoAddViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPotatoAddBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        bindViewModel()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenStarted { viewModel.isItemAdded.collect(::close) }
        lifecycleScope.launchWhenStarted { viewModel.isAddButtonEnabled.collect(::activateAddButton) }
        lifecycleScope.launchWhenStarted { viewModel.isToast.collect { if (it.isNotBlank()) toast(it) } }
        lifecycleScope.launchWhenStarted {
            viewModel.isNameFieldError.collect {
                views {
                    name.editText?.error = if (it) "error" else null
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isDescriptionFieldError.collect {
                views {
                    description.editText?.error = if (it) "error" else null
                }
            }
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
        }
    }

    private fun insertPotato() {
        views {
            addPotatoButton.isEnabled = false
            name.isEnabled = false
            description.isEnabled = false
            imageUrl.isEnabled = false
            viewModel.add(
                name = name.editText?.text.toString(),
                description = description.editText?.text.toString(),
                imageUrl = imageUrl.editText?.text.toString()
            )
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
//            findNavController().popBackStack()
        }
    }

    private fun <T> views(block: FragmentPotatoAddBinding.() -> T): T? = binding?.block()
}
