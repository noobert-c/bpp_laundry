package com.example.bpp_laundry.paketharga

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bpp_laundry.data.repository.CheckPointRepository
import com.example.bpp_laundry.data.repository.ColorRepository
import com.example.bpp_laundry.data.repository.PackageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PkgCheckPointUi(
    val id: Int,
    val name: String,
    val colorName: String = "",
    val color: Color = Color(0xFF3B82F6)
)

data class ServicePackageUi(
    val id: Int,
    val name: String,
    val pricePerKg: Int,
    val days: Int,
    val checkPoints: List<PkgCheckPointUi> = emptyList()
)

class PaketHargaViewModel(
    private val packageRepository: PackageRepository = PackageRepository(),
    private val checkPointRepository: CheckPointRepository = CheckPointRepository(),
    private val colorRepository: ColorRepository = ColorRepository()
) : ViewModel() {

    private val _packages = MutableStateFlow<List<ServicePackageUi>>(emptyList())
    val packages: StateFlow<List<ServicePackageUi>> = _packages.asStateFlow()

    private val _catalogCheckpoints = MutableStateFlow<List<PkgCheckPointUi>>(emptyList())
    val catalogCheckpoints: StateFlow<List<PkgCheckPointUi>> = _catalogCheckpoints.asStateFlow()

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

            // 1. Ambil data warna & checkpoints master untuk katalog
            val colorsMap = colorRepository.getColors().getOrElse { emptyList() }.associateBy { it.id }
            val allCheckpoints = checkPointRepository.getCheckpoints().getOrElse { emptyList() }

            val catalog = allCheckpoints.map { cp ->
                val colorObj = colorsMap[cp.colorId]
                PkgCheckPointUi(
                    id = cp.id,
                    name = cp.name,
                    colorName = colorObj?.name ?: "",
                    color = colorObj?.toComposeColor() ?: Color(0xFF3B82F6)
                )
            }
            _catalogCheckpoints.value = catalog
            val catalogMap = catalog.associateBy { it.id }

            // 2. Ambil data paket laundry
            val packagesResult = packageRepository.getPackages()
            val packageList = packagesResult.getOrElse { emptyList() }

            // 3. Ambil data relasi package_checkpoints
            val relationsResult = packageRepository.getPackageCheckpoints()
            val relations = relationsResult.getOrElse { emptyList() }
            val relationsByPackage = relations.groupBy { it.packageId }

            // 4. Gabungkan paket dengan check point terkait
            _packages.value = packageList.map { pkg ->
                val pkgRelations = relationsByPackage[pkg.id].orEmpty()
                val pkgCps = pkgRelations.mapNotNull { rel ->
                    catalogMap[rel.checkpointId] ?: PkgCheckPointUi(
                        id = rel.checkpointId,
                        name = rel.checkpointName ?: "Checkpoint ${rel.checkpointId}",
                        colorName = "",
                        color = Color(0xFF64748B)
                    )
                }
                ServicePackageUi(
                    id = pkg.id,
                    name = pkg.name,
                    pricePerKg = pkg.pricePerKg.toInt(),
                    days = pkg.estDays,
                    checkPoints = pkgCps
                )
            }

            if (packagesResult.isFailure) {
                _errorMessage.value = packagesResult.exceptionOrNull()?.message
            }

            _isLoading.value = false
        }
    }

    fun createPackage(
        name: String,
        pricePerKg: Int,
        days: Int,
        checkpointIds: List<Int>,
        onSuccess: () -> Unit = {}
    ) {
        if (name.isBlank()) {
            _errorMessage.value = "Nama paket tidak boleh kosong"
            return
        }
        if (pricePerKg <= 0) {
            _errorMessage.value = "Harga per kg harus lebih besar dari 0"
            return
        }
        if (days <= 0) {
            _errorMessage.value = "Estimasi pengerjaan minimal 1 hari"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val pkgResult = packageRepository.createPackage(
                name = name.trim(),
                pricePerKg = pricePerKg.toDouble(),
                estDays = days
            )

            pkgResult.onSuccess { createdPkg ->
                // Tambahkan relasi checkpoint ke paket
                for (cpId in checkpointIds) {
                    packageRepository.createPackageCheckpoint(
                        packageId = createdPkg.id,
                        checkpointId = cpId
                    )
                }
                _successMessage.value = "Paket berhasil ditambahkan"
                loadData()
                onSuccess()
            }.onFailure { err ->
                _errorMessage.value = err.message
                _isLoading.value = false
            }
        }
    }

    fun updatePackage(
        id: Int,
        name: String,
        pricePerKg: Int,
        days: Int,
        checkpointIds: List<Int>,
        onSuccess: () -> Unit = {}
    ) {
        if (name.isBlank()) {
            _errorMessage.value = "Nama paket tidak boleh kosong"
            return
        }
        if (pricePerKg <= 0) {
            _errorMessage.value = "Harga per kg harus lebih besar dari 0"
            return
        }
        if (days <= 0) {
            _errorMessage.value = "Estimasi pengerjaan minimal 1 hari"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val pkgResult = packageRepository.updatePackage(
                id = id,
                name = name.trim(),
                pricePerKg = pricePerKg.toDouble(),
                estDays = days
            )

            pkgResult.onSuccess {
                // Selaraskan relasi checkpoint
                packageRepository.syncPackageCheckpoints(
                    packageId = id,
                    newCheckpointIds = checkpointIds
                )
                _successMessage.value = "Paket berhasil diperbarui"
                loadData()
                onSuccess()
            }.onFailure { err ->
                _errorMessage.value = err.message
                _isLoading.value = false
            }
        }
    }

    fun deletePackage(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = packageRepository.deletePackage(id = id)
            result.onSuccess {
                _successMessage.value = "Paket berhasil dihapus"
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
