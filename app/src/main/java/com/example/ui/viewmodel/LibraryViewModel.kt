package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.LocalMaterialEntity
import com.example.data.model.ExamCatalog
import com.example.data.model.StudyMaterial
import com.example.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryUiState(
    val materials: List<StudyMaterial> = ExamCatalog.defaultMaterials,
    val savedOfflineIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val selectedCategory: String = "Tất cả",
    val selectedMaterial: StudyMaterial? = null
)

class LibraryViewModel(
    private val firestoreRepo: FirestoreRepository,
    private val localDb: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadMaterials()
    }

    private fun loadMaterials() {
        viewModelScope.launch {
            localDb.materialDao().getAllMaterials().collect { localList ->
                _uiState.value = _uiState.value.copy(
                    savedOfflineIds = localList.map { it.id }.toSet()
                )
            }
        }

        viewModelScope.launch {
            try {
                firestoreRepo.observeStudyMaterials().collect { cloudMaterials ->
                    if (cloudMaterials.isNotEmpty()) {
                        val merged = (ExamCatalog.defaultMaterials + cloudMaterials).distinctBy { it.materialId }
                        _uiState.value = _uiState.value.copy(materials = merged)
                    }
                }
            } catch (e: Exception) {
                // Keep default catalog if offline
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectCategory(cat: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = cat)
    }

    fun selectMaterial(material: StudyMaterial?) {
        _uiState.value = _uiState.value.copy(selectedMaterial = material)
    }

    fun toggleSaveOffline(material: StudyMaterial) {
        viewModelScope.launch {
            val isCurrentlySaved = material.materialId in _uiState.value.savedOfflineIds
            if (isCurrentlySaved) {
                localDb.materialDao().deleteById(material.materialId)
            } else {
                localDb.materialDao().insert(
                    LocalMaterialEntity(
                        id = material.materialId,
                        title = material.title,
                        subject = material.subject,
                        category = material.category,
                        summary = material.summary,
                        content = material.content,
                        isSavedOffline = true
                    )
                )
            }
        }
    }
}
