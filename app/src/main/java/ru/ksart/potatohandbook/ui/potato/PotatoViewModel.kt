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
    private var updateChangeFilterJob: Job? = null

    private val _potatoes = MutableStateFlow<List<Potato>>(emptyList())
    val potatoes: StateFlow<List<Potato>> get() = _potatoes.asStateFlow()

    private val _isToast = MutableStateFlow("")
    val isToast get() = _isToast.asStateFlow()

    val subTitle get() = repository.dbmsName

    private val listFlow = MutableStateFlow<List<Potato>>(emptyList())
    private val stateFlow = MutableStateFlow<PotatoState?>(null)
    private val changeFilter = repository.changeFilter

    private val searchFlow = MutableStateFlow("")
    private val searchLength = 1

    init {
        DebugHelper.log("PotatoViewModel|init ${this.hashCode()}")
        initFilterListener()
        updateListFiltered()
    }

    private fun initFilterListener() {
        viewModelScope.launch {
            repository.registerChangeFilter()
        }

        updateChangeFilterJob = changeFilter.onEach {
            DebugHelper.log("PotatoViewModel|changeFilter n=$it")
            stateFlow.value = repository.readFilter().also { stateNew ->
                DebugHelper.log("PotatoViewModel|stateFlow ${stateFlow.value?.dbms}=${stateNew.dbms}")
                stateFlow.value?.let { state ->
                    if (state.dbms != stateNew.dbms) updateListFromDb()
                } ?: updateListFromDb()
            }
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun unregisterChangeFilter() {
        viewModelScope.launch {
            repository.unregisterChangeFilter()
        }
    }

    private fun updateListFromDb() {
        updateListFromDbJob?.takeIf { it.isActive }?.cancel()
        DebugHelper.log("PotatoViewModel|updateListFromDb clear list")
        _potatoes.value = emptyList()
        listFlow.value = emptyList()
        updateListFromDbJob = repository.getPotatoAll()
            .flowOn(Dispatchers.IO)
            .onEach {
                DebugHelper.log("PotatoViewModel|updateListFromDb list=${it.size} на потоке ${Thread.currentThread().name}")
                listFlow.value = it
            }
            .catch {
                DebugHelper.log("PotatoViewModel|updateListFiltered error list")
                listFlow.value = emptyList()
            }
            .launchIn(viewModelScope)
    }

    private fun updateListFiltered() {
        updateListFilteredJob = combine(
            listFlow.debounce(250),
            stateFlow.debounce(250),
            searchFlow.debounce(250)
                .distinctUntilChanged()
                // не делать запрос короче 3 символов
                .filter { it.isBlank() || it.length > searchLength },
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
        DebugHelper.log("PotatoViewModel|updateList in list=${listFromDb.size}")
        if (state == null && searchName == "") return listFromDb.sortedBy { it.name }
        val list = listFromDb.takeIf { it.isNotEmpty() }?.mapNotNull { potato ->
            if ((searchName.isBlank() || searchName.length <= searchLength ||
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

    fun delete(item: Potato) {
        viewModelScope.launch {
            try {
                repository.delete(item)
            } catch (e: Throwable) {
                _isToast.value = "Error deleting record id=${item.id}\n${e.localizedMessage}"
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
            } catch (e: Throwable) {
                _isToast.value = "Error deleting all records\n${e.localizedMessage}"
            }
        }
    }

    fun initData() {
        viewModelScope.launch {
            try {
                repository.initData()
            } catch (e: Throwable) {
                _isToast.value = "Error init data\n${e.localizedMessage}"
            }
        }
    }

    override fun onCleared() {
        // отменим задание
        cancelJob()
        unregisterChangeFilter()
        super.onCleared()
    }

    private fun cancelJob() {
        // отменим задание
        updateListFromDbJob?.takeIf { it.isActive }?.cancel()
        updateListFilteredJob?.takeIf { it.isActive }?.cancel()
        updateChangeFilterJob?.takeIf { it.isActive }?.cancel()
    }
}
