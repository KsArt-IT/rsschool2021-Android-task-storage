package ru.ksart.potatohandbook.ui.potato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ksart.potatohandbook.model.data.PeriodRipening
import ru.ksart.potatohandbook.model.data.PotatoVariety
import ru.ksart.potatohandbook.model.data.Productivity
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.repository.PotatoRepository
import ru.ksart.potatohandbook.utils.DebugHelper
import javax.inject.Inject

@HiltViewModel
class PotatoViewModel @Inject constructor(
    private val repository: PotatoRepository
) : ViewModel() {

    private var updateJob: Job? = null

    private val _potatoes = MutableStateFlow<List<Potato>>(emptyList())
    val potatoes: StateFlow<List<Potato>> get() = _potatoes.asStateFlow()

    private val listFlow = repository.getPotatoAll()

    private val searchFlow = MutableStateFlow("")
    private val sortNameFlow = MutableStateFlow(true)
    private val filterFlow =
        MutableStateFlow<Triple<PotatoVariety?, PeriodRipening?, Productivity?>>(
            Triple(
                null,
                null,
                null
            )
        )

    init {
        DebugHelper.log("PotatoViewModel|init ${this.hashCode()}")
        // при изменении одного из параметров, пересоздаем список
            viewModelScope.launch {
                updateListPotato(
                    // поиск по названию
                    search = searchFlow
                        .debounce(250)
                        .distinctUntilChanged(),
                    // сортировка по названию или как есть
                    sorting = sortNameFlow,
                    // фильтр по сорту, сроку созревания, урожайность
                    filter = filterFlow
                        .debounce(250)
                        .distinctUntilChanged(),
                    // получение списка из DB
                    list = repository.getPotatoAll()
                        .distinctUntilChanged()
                )
            }
/*
        // получение списка из DB
        repository.getPotatoAll()
            .distinctUntilChanged()
            .onEach { list ->
                DebugHelper.log("PotatoViewModel|init list=${list.size}")
                _potatoes.value = list
            }.launchIn(viewModelScope)
        // сортировка по названию или как есть

        // поиск по названию
        searchFlow
            .debounce(250)
            .distinctUntilChanged()
            .filter { it.length > 2 }
            .onEach(::updateFiltered)
            .launchIn(viewModelScope)
        // фильтр по сорту, сроку созревания, урожайность
        filterFlow
            .debounce(250)
            .distinctUntilChanged()

            .launchIn(viewModelScope)
*/
/*
            updateJob = combine(
                searchFlow
                    .debounce(250)
                    .distinctUntilChanged(),
                sortNameFlow,
                filterFlow
                    .debounce(250)
                    .distinctUntilChanged(),
                repository.getPotatoAll()
                    .distinctUntilChanged()
            ) { searchName, isSortName, filterPotato, listFromDb ->
                DebugHelper.log("PotatoViewModel|updateListPotato in list=${listFromDb.size}")
                val list = listFromDb.takeIf { it.isNotEmpty() }?.mapNotNull { potato ->
                    if ((searchName.length <= 2 || potato.name.contains(searchName, ignoreCase = false)) &&
                        ((filterPotato.first == null && filterPotato.second == null && filterPotato.third == null) ||
                                (filterPotato.first != null && potato.variety == filterPotato.first) ||
                                (filterPotato.second != null && potato.ripening == filterPotato.second) ||
                                (filterPotato.third != null && potato.productivity == filterPotato.third))) potato
                    else null
                }.orEmpty()
                DebugHelper.log("PotatoViewModel|updateListPotato out list=${list.size}")
                if (isSortName) list.sortedBy { it.name }
                else list
            }
                // выполнять не чаще ... мс
                .debounce(250)
                // не повторять одинаковые запросы
                .distinctUntilChanged()
                // выполнять
                .flowOn(Dispatchers.IO)
                .onEach {
                    _potatoes.value = it
                }
                .launchIn(viewModelScope)
*/
    }

    @FlowPreview
    private fun updateListPotato(
        search: Flow<String>,
        sorting: Flow<Boolean>,
        filter: Flow<Triple<PotatoVariety?, PeriodRipening?, Productivity?>>,
        list: Flow<List<Potato>>
    ) {
        DebugHelper.log("PotatoViewModel|updateListPotato in")
        updateJob = combine(
            search,
            sorting,
            filter,
            list
        ) { searchName, isSortName, filterPotato, listFromDb ->
            DebugHelper.log("PotatoViewModel|updateListPotato in list=${listFromDb.size}")
            val list = listFromDb.takeIf { it.isNotEmpty() }?.mapNotNull { potato ->
                if ((searchName.length <= 2 || potato.name.contains(searchName, ignoreCase = false)) &&
                    ((filterPotato.first == null && filterPotato.second == null && filterPotato.third == null) ||
                    (filterPotato.first != null && potato.variety == filterPotato.first) ||
                    (filterPotato.second != null && potato.ripening == filterPotato.second) ||
                    (filterPotato.third != null && potato.productivity == filterPotato.third))) potato
                else null
            }.orEmpty()
            DebugHelper.log("PotatoViewModel|updateListPotato out list=${list.size}")
            if (isSortName) list.sortedBy { it.name }
            else list.sortedByDescending { it.name }
        }
            // выполнять не чаще ... мс
            .debounce(250)
            // не повторять одинаковые запросы
            .distinctUntilChanged()
                // выполнять
            .flowOn(Dispatchers.IO)
            .onEach {
                _potatoes.value = it
            }
            .launchIn(viewModelScope)
    }

    fun searchName(searchText: String) {
        searchFlow.value = searchText
    }

    fun sortName(sort: Boolean) {
        sortNameFlow.value = sort
    }

    private fun filtered(variety: PotatoVariety?, ripening: PeriodRipening?, productivity: Productivity?) {
        filterFlow.value = Triple(variety, ripening, productivity)
    }

    // обновление фильтра
    fun readFilter() {
        viewModelScope.launch {
            val (name, filter) = repository.readFilter()
            sortName(name)
            filtered(filter.first, filter.second, filter.third)
        }
    }

    fun delete(item: Potato) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun initData() {
        viewModelScope.launch {
            repository.initData()
        }
    }

    override fun onCleared() {
        // отменим задание
        cancelJob()
        super.onCleared()
    }

    private fun cancelJob() {
        // отменим задание
        updateJob?.takeIf { it.isActive }?.cancel()
    }
}
