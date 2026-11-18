package com.example.bpp_laundry.checkpoint

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bpp_laundry.data.model.ColorResponse
import com.example.bpp_laundry.data.repository.CheckPointRepository
import com.example.bpp_laundry.data.repository.ColorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckPointUiModel(
    val id: Int,
    val name: String,
    val colorId: Int,
    val color: Color
)

class CheckPointViewModel(
    private val checkPointRepository: CheckPointRepository = CheckPointRepository(),
    private val colorRepository: ColorRepository = ColorRepository()
) : ViewModel() {

    private val _checkpoints = MutableStateFlow<List<CheckPointUiModel>>(emptyList())
    val checkpoints: StateFlow<List<CheckPointUiModel>> = _checkpoints.asStateFlow()

    private val _colors = MutableStateFlow<List<ColorResponse>>(emptyList())
    val colors: StateFlow<List<ColorResponse>> = _colors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // 1. Ambil data warna
            val colorsResult = colorRepository.getColors()
            val colorsList = colorsResult.getOrElse { emptyList() }
            _colors.value = colorsList

            val colorMap = colorsList.associateBy { it.id }

            // 2. Ambil data checkpoint
            val cpResult = checkPointRepository.getCheckpoints()
            cpResult.onSuccess { list ->
                _checkpoints.value = list.map { cp ->
                    val colorObj = colorMap[cp.colorId]
                    val composeColor = colorObj?.toComposeColor() ?: Color(0xFF3B82F6)
                    CheckPointUiModel(
                        id = cp.id,
                        name = cp.name,
                        colorId = cp.colorId,
                        color = composeColor
                    )
                }
            }.onFailure { err ->
                _errorMessage.value = err.message
            }

            _isLoading.value = false
        }
    }

    fun createCheckpoint(name: String, colorId: Int, onSuccess: () -> Unit = {}) {
        if (name.isBlank()) {
            _errorMessage.value = "Nama check point tidak boleh kosong"
            return
        }
        if (colorId <= 0) {
            _errorMessage.value = "Pilih warna terlebih dahulu"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = checkPointRepository.createCheckpoint(colorId = colorId, name = name.trim())
            result.onSuccess {
                _successMessage.value = "Checkpoint berhasil ditambahkan"
                // Update status is_used warna jika ada di daftar warna
                val colorObj = _colors.value.firstOrNull { it.id == colorId }
                if (colorObj != null) {
                    colorRepository.updateColor(
                        id = colorId,
                        name = colorObj.name,
                        color = colorObj.color,
                        isUsed = true
                    )
                }
                loadData()
                onSuccess()
            }.onFailure { err ->
                _errorMessage.value = err.message
                _isLoading.value = false
            }
        }
    }

    fun updateCheckpoint(
        id: Int,
        name: String,
        colorId: Int,
        oldColorId: Int? = null,
        onSuccess: () -> Unit = {}
    ) {
        if (name.isBlank()) {
            _errorMessage.value = "Nama check point tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = checkPointRepository.updateCheckpoint(id = id, colorId = colorId, name = name.trim())
            result.onSuccess {
                _successMessage.value = "Checkpoint berhasil diperbarui"
                // Perbarui status warna baru menjadi is_used = true
                val newColorObj = _colors.value.firstOrNull { it.id == colorId }
                if (newColorObj != null) {
                    colorRepository.updateColor(
                        id = colorId,
                        name = newColorObj.name,
                        color = newColorObj.color,
                        isUsed = true
                    )
                }
                // Jika warna berubah, lepaskan status warna lama (is_used = false)
                if (oldColorId != null && oldColorId != colorId) {
                    val otherUses = _checkpoints.value.any { it.id != id && it.colorId == oldColorId }
                    if (!otherUses) {
                        val oldColorObj = _colors.value.firstOrNull { it.id == oldColorId }
                        if (oldColorObj != null) {
                            colorRepository.updateColor(
                                id = oldColorId,
                                name = oldColorObj.name,
                                color = oldColorObj.color,
                                isUsed = false
                            )
                        }
                    }
                }
                loadData()
                onSuccess()
            }.onFailure { err ->
                _errorMessage.value = err.message
                _isLoading.value = false
            }
        }
    }

    fun deleteCheckpoint(id: Int, colorId: Int? = null, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = checkPointRepository.deleteCheckpoint(id = id)
            result.onSuccess {
                _successMessage.value = "Checkpoint berhasil dihapus"
                // Jika warna tidak lagi digunakan oleh checkpoint lain, set is_used = false
                if (colorId != null) {
                    val otherUses = _checkpoints.value.any { it.id != id && it.colorId == colorId }
                    if (!otherUses) {
                        val colorObj = _colors.value.firstOrNull { it.id == colorId }
                        if (colorObj != null) {
                            colorRepository.updateColor(
                                id = colorId,
                                name = colorObj.name,
                                color = colorObj.color,
                                isUsed = false
                            )
                        }
                    }
                }
                loadData()
                onSuccess()
            }.onFailure { err ->
                _errorMessage.value = err.message
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
