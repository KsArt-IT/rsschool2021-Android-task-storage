package ru.ksart.potatohandbook.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.databinding.ActivityMainBinding
import ru.ksart.potatohandbook.ui.ShowMenu
import ru.ksart.potatohandbook.ui.potato.PotatoViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ShowMenu {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val viewModel by viewModels<PotatoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityMainBinding.inflate(layoutInflater).also { binding = it }.root
        )
        initAppBar()
        initToolbarMenu()
    }

    private fun initAppBar() {
//        navController = findNavController(this, R.id.fragmentContainerView)
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
        // обработаем AppBarConfiguration, прописать все верхние уровни id
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.potatoFragment,
            )
        )
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)
    }

    private fun initToolbarMenu() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_filter -> {
                    showToolbarMenu(false)
                    showFilterFragment()
                    true
                }
                R.id.action_add_init_data -> {
                    viewModel.initData()
                    true
                }
                R.id.action_clear_db -> {
                    viewModel.deleteAll()
                    true
                }
                else -> false
            }
        }
        initSearch()
    }

    private fun initSearch() {
        // обработка меню поиск
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        // обработка поиска
        (searchItem.actionView as SearchView).setOnQueryTextListener(
            object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        viewModel.searchName(it)
                    }
                    return true
                }
            }
        )
    }

    private fun showFilterFragment() {
        navController.navigate(R.id.action_potatoFragment_to_filterFragment)
    }

    private fun showToolbarMenu(show: Boolean) {
        binding.toolbar.menu.setGroupVisible(0, show)
    }

    override fun showMenu(show: Boolean) {
        showToolbarMenu(show)
    }
}