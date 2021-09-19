package ru.ksart.potatohandbook.ui.potato

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.databinding.FragmentPotatoBinding
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.ui.ShowMenu
import ru.ksart.potatohandbook.ui.potato.adapter.PotatoAdapter
import ru.ksart.potatohandbook.ui.potato.adapter.SwipeHelper
import ru.ksart.potatohandbook.utils.DebugHelper

@AndroidEntryPoint
class PotatoFragment : Fragment() {

    private var binding: FragmentPotatoBinding? = null

    private val viewModel by activityViewModels<PotatoViewModel>()

    private val potatoAdapter: PotatoAdapter? get() = views { potatoList.adapter as? PotatoAdapter }

    private val parent get() = activity?.let { it as? ShowMenu }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPotatoBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DebugHelper.log("PotatoFragment|onViewCreated ${this.hashCode()}")
        parent?.showMenu(show = true)
        initAdapter()
        bindViewModel()
        initListener()
        // для анимированного перехода
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }
//        views { potatoList.doOnPreDraw { startPostponedEnterTransition() } }
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
        lifecycleScope.launchWhenStarted { viewModel.potatoes.collect(::showList) }
        lifecycleScope.launchWhenStarted { viewModel.changeFilter.debounce(250).collect { viewModel.readFilter() } }
    }

    private fun showList(list: List<Potato>) {
        DebugHelper.log("PotatoFragment|showList list=${list.size}")
        potatoAdapter?.submitList(list)
        views { dbEmpty.isVisible = list.isEmpty() }
    }

    private fun showDetail(item: Potato, imageView: ImageView) {
        DebugHelper.log("PotatoFragment|showDetail list=${item.name}")
        val extras = FragmentNavigatorExtras(
            // установим переход для анимации
            imageView to item.id.toString()
        )
        val action = PotatoFragmentDirections.actionPotatoFragmentToPotatoDetailFragment(item)
        findNavController().navigate(action, extras)
    }

    private fun initListener() {
        views {
            addPotatoButton.setOnClickListener { addPotato() }
        }
    }

    private fun addPotato() {
        findNavController().navigate(R.id.action_potatoFragment_to_potatoAddFragment)
    }

    private fun <T> views(block: FragmentPotatoBinding.() -> T): T? = binding?.block()
}
