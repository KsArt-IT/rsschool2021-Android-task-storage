package ru.ksart.potatohandbook.ui.potato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ksart.potatohandbook.model.data.PotatoState
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.repository.PotatoRepository
import ru.ksart.potatohandbook.utils.DebugHelper
import javax.inject.Inject

@HiltViewModel
class PotatoViewModel @Inject constructor(
    private val repository: PotatoRepository
) : ViewModel() {

    private var updateListFromDbJob: Job? = null
    private var updateListFilteredJob: Job? = null

    private val _potatoes = MutableStateFlow<List<Potato>>(emptyList())
    val potatoes: StateFlow<List<Potato>> get() = _potatoes.asStateFlow()

    val subTitle get() = repository.dbmsName

    private val listFlow = MutableStateFlow<List<Potato>>(emptyList())
    private val stateFlow = MutableStateFlow<PotatoState?>(null)
    private val searchFlow = MutableStateFlow("")

    init {
        DebugHelper.log("PotatoViewModel|init ${this.hashCode()}")
        readFilter(true)
        updateListFromDb()
        updateListFiltered()
    }

    private fun updateListFromDb() {
        updateListFromDbJob?.takeIf { it.isActive }?.cancel()
        updateListFromDbJob = repository.getPotatoAll().onEach {
            DebugHelper.log("PotatoViewModel|updateListFromDb list=${it.size}")
            listFlow.value = it
        }.launchIn(viewModelScope)
    }

    private fun updateListFiltered() {
        updateListFilteredJob = combine(
            listFlow.debounce(250),
            stateFlow.debounce(250),
            searchFlow.debounce(250)
                .distinctUntilChanged(),
            ::updateList
        )
            // выполнять не чаще ... мс
            .debounce(500)
            // не повторять одинаковые запросы, но разные дао дают одинаковый результат
//            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .onEach {
                DebugHelper.log("PotatoViewModel|updateListFiltered list updated")
                _potatoes.value = it
            }
            .catch {
                DebugHelper.log("PotatoViewModel|updateListFiltered error list")
                _potatoes.value = emptyList()
            }
            .launchIn(viewModelScope)
    }

    private fun updateList(
        listFromDb: List<Potato>,
        state: PotatoState?,
        searchName: String
    ): List<Potato> {
        DebugHelper.log("PotatoViewModel|updateList in")
        DebugHelper.log("PotatoViewModel|updateList in list=${listFromDb.size}")
        if (state == null && searchName == "") return listFromDb.sortedBy { it.name }
        val list = listFromDb.takeIf { it.isNotEmpty() }?.mapNotNull { potato ->
            if ((searchName.length <= 2 ||
                        potato.name.contains(searchName, ignoreCase = true)) &&
                ((state == null) ||
                        (state.filter.variety == null && state.filter.ripening == null && state.filter.productivity == null) ||
                        (state.filter.variety?.takeIf { it == potato.variety } != null) ||
                        (state.filter.ripening?.takeIf { it == potato.ripening } != null) ||
                        (state.filter.productivity?.takeIf { it == potato.productivity } != null))
            ) potato
            else null
        }.orEmpty()
        DebugHelper.log("PotatoViewModel|updateList out list=${list.size}")
        return if (state == null || state.sortName) list.sortedBy { it.name }
        else list.sortedByDescending { it.name }
    }

    fun searchName(searchText: String) {
        searchFlow.value = searchText
    }

    // обновление фильтра
    fun readFilter(init: Boolean = false) {
        viewModelScope.launch {
            DebugHelper.log("---------------------readFilter---------------------------")
            val state = repository.readFilter()
            stateFlow.value?.takeIf { (it.dbms != state.dbms) || init }
                ?.let {
                    DebugHelper.log("PotatoViewModel|readFilter clear list")
                    _potatoes.value = emptyList()
                    listFlow.value = emptyList()
                    updateListFromDb()
                }
            stateFlow.value = state
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
        updateListFromDbJob?.takeIf { it.isActive }?.cancel()
        updateListFilteredJob?.takeIf { it.isActive }?.cancel()
    }
}
