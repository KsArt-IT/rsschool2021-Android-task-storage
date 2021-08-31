package ru.ksart.potatohandbook.ui.potato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.repository.PotatoRepository
import ru.ksart.potatohandbook.utils.DebugHelper
import javax.inject.Inject

@HiltViewModel
class PotatoViewModel @Inject constructor(
    private val repository: PotatoRepository
) : ViewModel() {
    private val _potatoes = MutableStateFlow<List<Potato>>(emptyList())
    val potatoes: StateFlow<List<Potato>> get() = _potatoes.asStateFlow()

    init {
        DebugHelper.log("PotatoViewModel|init ${this.hashCode()}")
        repository.getPotatoAll()
//            .debounce(250)
            .distinctUntilChanged()
            .onEach { list ->
                DebugHelper.log("PotatoViewModel|init list=${list.size}")
                _potatoes.value = list
            }.launchIn(viewModelScope)
    }

    fun delete(item: Potato) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }
}
