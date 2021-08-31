package ru.ksart.potatohandbook.ui.potato

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ksart.potatohandbook.databinding.FragmentPotatoBinding
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.ui.potato.adapter.PotatoAdapter
import ru.ksart.potatohandbook.ui.potato.adapter.SwipeHelper
import ru.ksart.potatohandbook.utils.DebugHelper

@AndroidEntryPoint
class PotatoFragment : Fragment() {

    private var binding: FragmentPotatoBinding? = null

    private val viewModel by viewModels<PotatoViewModel>()

    private val potatoAdapter: PotatoAdapter? get() = views { potatoList.adapter as? PotatoAdapter }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPotatoBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DebugHelper.log("PotatoFragment|onViewCreated ${this.hashCode()}")
        initAdapter()
        bindViewModel()
        initListener()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun initAdapter() {
        views {
            potatoList.run {
                adapter = PotatoAdapter(::showDetail)
                layoutManager = LinearLayoutManager(requireContext().applicationContext)
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
            }
            SwipeHelper(viewModel::delete).attachToRecyclerView(potatoList)
        }
    }

    private fun bindViewModel() {
        viewModel.run {
            lifecycleScope.launchWhenStarted { viewModel.potatoes.collect(::showList) }
            viewModel.potatoes.onEach { showList(it) }.launchIn(lifecycleScope)
//            lifecycleScope.launchWhenStarted { viewModel.potatoes.collect(::showList) }
        }
    }

    private fun showList(list: List<Potato>) {
        DebugHelper.log("PotatoFragment|showList list=${list.size}")
        potatoAdapter?.submitList(list)
        views { dbEmpty.isVisible = list.isEmpty() }
    }

    private fun showDetail(item: Potato) {
        DebugHelper.log("PotatoFragment|showDetail list=${item.name}")

    }

    private fun initListener() {
        views {
            addPotatoButton.setOnClickListener { addPotato() }
        }
    }

    private fun addPotato() {
//        findNavController().navigate(R.id.action_potatoFragment_to_potatoAddFragment)
    }

    private fun <T> views(block: FragmentPotatoBinding.() -> T): T? = binding?.block()
}
