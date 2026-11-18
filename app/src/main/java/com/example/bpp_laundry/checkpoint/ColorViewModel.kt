package com.example.bpp_laundry.checkpoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bpp_laundry.data.model.ColorResponse
import com.example.bpp_laundry.data.repository.ColorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColorViewModel(
    private val repository: ColorRepository = ColorRepository()
) : ViewModel() {

    private val _colors = MutableStateFlow<List<ColorResponse>>(emptyList())
    val colors: StateFlow<List<ColorResponse>> = _colors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchColors()
    }

    fun fetchColors() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.getColors()
            result.onSuccess { list ->
                _colors.value = list
            }.onFailure { err ->
                _errorMessage.value = err.message
            }
            _isLoading.value = false
        }
    }
}
