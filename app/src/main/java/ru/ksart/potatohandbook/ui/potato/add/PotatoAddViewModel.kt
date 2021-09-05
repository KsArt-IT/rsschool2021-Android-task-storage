package ru.ksart.potatohandbook.ui.potato.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.ksart.potatohandbook.model.data.PeriodRipening
import ru.ksart.potatohandbook.model.data.PotatoVariety
import ru.ksart.potatohandbook.model.data.Productivity
import ru.ksart.potatohandbook.model.db.Potato
import ru.ksart.potatohandbook.model.repository.PotatoRepository
import ru.ksart.potatohandbook.utils.DebugHelper
import javax.inject.Inject

@HiltViewModel
class PotatoAddViewModel @Inject constructor(
    private val repository: PotatoRepository
) : ViewModel() {

    private val _isToast = MutableStateFlow("")
    val isToast: StateFlow<String> get() = _isToast.asStateFlow()

    private val _isItemAdded = MutableStateFlow(false)
    val isItemAdded: StateFlow<Boolean> get() = _isItemAdded.asStateFlow()

    private val _isAddButtonEnabled = MutableStateFlow(false)
    val isAddButtonEnabled: StateFlow<Boolean> get() = _isAddButtonEnabled.asStateFlow()

    private val _isNameFieldError = MutableStateFlow(false)
    val isNameFieldError: StateFlow<Boolean> get() = _isNameFieldError.asStateFlow()

    private val _isDescriptionFieldError = MutableStateFlow(false)
    val isDescriptionFieldError: StateFlow<Boolean> get() = _isDescriptionFieldError.asStateFlow()



    fun checkNameField(field: String) {
        _isNameFieldError.value = field.isBlank()
        checkConditions()
    }

    fun checkDescriptionField(field: String) {
        _isDescriptionFieldError.value = field.isBlank()
        checkConditions()
    }

    private fun checkConditions() {
        _isAddButtonEnabled.value = _isDescriptionFieldError.value.not() && _isNameFieldError.value.not()
        DebugHelper.log("PotatoAddViewModel|checkConditions button=${_isAddButtonEnabled.value}")
    }

    fun add(
        name: String,
        description: String,
        imageUrl: String,
        variety: String,
        ripening: String,
        productivity: String,
    ) {
        viewModelScope.launch {
            try {
                checkNameField(name)
                checkDescriptionField(description)
                if (_isDescriptionFieldError.value || _isNameFieldError.value) return@launch
                val imageUri = if (imageUrl.isNotBlank()) repository.downloadImage(name, imageUrl) else null
                repository.add(
                    item = Potato(
                        id = 0,
                        name = name,
                        description = description,
                        imageUri = imageUri,
                        imageUrl = if (imageUrl.isNotBlank()) imageUrl else null,
                        variety = PotatoVariety.values()[variety.toIntOrNull() ?: 0],
                        ripening = PeriodRipening.values()[ripening.toIntOrNull() ?: 0],
                        productivity = Productivity.values()[productivity.toIntOrNull() ?: 0],
                    )
                )
                _isItemAdded.value = true
            } catch (e: Exception) {
                _isToast.value = "Проверьте введенные данные!\n${e.localizedMessage}"
            }
        }
    }
}
