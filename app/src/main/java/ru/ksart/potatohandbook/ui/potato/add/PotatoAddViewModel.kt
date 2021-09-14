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

    private val _isItemAdded = MutableStateFlow(-1)
    val isItemAdded get() = _isItemAdded.asStateFlow()

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
        id: Long = 0,
        name: String,
        description: String,
        imageUrl: String,
        variety: Int,
        ripening: Int,
        productivity: Int,
    ) {
        viewModelScope.launch {
            try {
                // сбросим
                _isItemAdded.value = -1
                checkNameField(name)
                checkDescriptionField(description)
                if (_isDescriptionFieldError.value || _isNameFieldError.value) return@launch
                val imageUri = repository.downloadImage(name, imageUrl)
                val item = Potato(
                    id = id,
                    name = name,
                    description = description,
                    imageUri = imageUri,
                    imageUrl = if (imageUrl.isNotBlank()) imageUrl else null,
                    variety = PotatoVariety.values()[variety],
                    ripening = PeriodRipening.values()[ripening],
                    productivity = Productivity.values()[productivity],
                )
                if (id == 0L) repository.add(item) else repository.updatePotato(item)
                // установим ок
                _isItemAdded.value = 1
            } catch (e: Exception) {
                _isToast.value = "Check the entered data!\n${e.localizedMessage}"
                // установим ошибку
                _isItemAdded.value = 0
            }
        }
    }
}
