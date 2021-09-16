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

    private var potato: Potato = Potato(
        id = 0,
        name = "",
        description = "",
        imageUri = null,
        imageUrl = null,
        variety = PotatoVariety.Na,
        ripening = PeriodRipening.Na,
        productivity = Productivity.Na
    )

    fun getPotato() = potato

    fun savePotato(potato: Potato) {
        this.potato = potato
    }

    fun saveName(name: String) {
        potato = potato.copy(name = name)
        checkNameField(name)
    }

    fun saveDescription(description: String) {
        potato = potato.copy(description = description)
        checkDescriptionField(description)
    }

    fun saveImageUrl(imageUrl: String?) {
        val url = imageUrl?.takeIf { it.isNotBlank() }
        potato = potato.copy(imageUrl = url)
    }

    fun saveVariety(variety: Int) {
        if (variety in PotatoVariety.values().indices)
            potato = potato.copy(variety = PotatoVariety.values()[variety])
    }

    fun saveRipening(ripening: Int) {
        if (ripening in PeriodRipening.values().indices)
            potato = potato.copy(ripening = PeriodRipening.values()[ripening])
    }

    fun saveProductivity(productivity: Int) {
        if (productivity in Productivity.values().indices)
            potato = potato.copy(productivity = Productivity.values()[productivity])
    }

    fun checkNameField(field: String) {
        _isNameFieldError.value = field.isBlank()
        checkConditions()
    }

    fun checkDescriptionField(field: String) {
        _isDescriptionFieldError.value = field.isBlank()
        checkConditions()
    }

    private fun checkConditions() {
        _isAddButtonEnabled.value =
            _isDescriptionFieldError.value.not() && _isNameFieldError.value.not()
        DebugHelper.log("PotatoAddViewModel|checkConditions button=${_isAddButtonEnabled.value}")
    }

    fun add() {
        viewModelScope.launch {
            try {
                // сбросим
                _isItemAdded.value = -1
                checkNameField(potato.name)
                checkDescriptionField(potato.description)
                if (_isDescriptionFieldError.value || _isNameFieldError.value) {
                    _isItemAdded.value = 0
                    return@launch
                }
                val imageUri = potato.imageUrl?.let { url ->
                    repository.downloadImage(potato.name, url)
                }
                if (potato.imageUri != imageUri) potato = potato.copy(imageUri = imageUri)
                DebugHelper.log("PotatoAddViewModel|add id=${potato.id}")
                val result = if (potato.id == 0L) repository.add(potato)
                    else repository.update(potato).toLong()
                // установим ок
                _isItemAdded.value = if (result > 0L) 1 else 0
            } catch (e: Exception) {
                _isToast.value = "Check the entered data!\n${e.localizedMessage}"
                // установим ошибку
                _isItemAdded.value = 0
            }
        }

    }

}
