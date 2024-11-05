package de.levithas.aixdroid.presentation.ui.modelmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AddNewAIModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.DeleteModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.file.Path
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val addNewAIModelUseCase: AddNewAIModelUseCase,
    private val getModelListUseCase: GetModelListUseCase,
    private val deleteModelUseCase: DeleteModelUseCase
) : ViewModel() {

    private val _allModels = MutableStateFlow<List<ModelData>>(emptyList())
    val allModels: StateFlow<List<ModelData>> get() = _allModels

    init {
        fetchAllModels()
    }

    private fun fetchAllModels() {
        viewModelScope.launch(Dispatchers.IO) {
            getModelListUseCase.invoke().collect { list ->
                _allModels.value = list
            }
        }
    }

    fun addModelConfiguration(path: Path) {
        viewModelScope.launch(Dispatchers.IO) {
            addNewAIModelUseCase(path)
        }
    }

    fun deleteModelConfiguration(path: Path) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteModelUseCase.invoke(path)
            fetchAllModels()
        }
    }
}