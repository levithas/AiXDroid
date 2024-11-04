package de.levithas.aixdroid.presentation.ui.modelmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.levithas.aixdroid.domain.model.ModelConfiguration
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AddNewAIModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AddNewAIModelUseCaseImpl
import de.levithas.aixdroid.domain.usecase.aimodelmanager.DeleteModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelByIdUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.tensorflow.lite.schema.Metadata
import java.nio.file.Path
import javax.inject.Inject

@HiltViewModel
open class AIViewModel @Inject constructor(
    private val addNewAIModelUseCase: AddNewAIModelUseCase,
    private val getModelListUseCase: GetModelListUseCase,
    private val getModelByIdUseCase: GetModelByIdUseCase,
    private val deleteModelUseCase: DeleteModelUseCase
) : ViewModel() {

    private val _allModels = MutableStateFlow<List<ModelConfiguration>>(emptyList())
    val allModels: StateFlow<List<ModelConfiguration>> get() = _allModels

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

    fun addModelConfiguration(name: String, path: Path, metadata: Metadata) {
        viewModelScope.launch(Dispatchers.IO) {
            addNewAIModelUseCase(name, path, metadata)
        }
    }

    fun deleteModelConfiguration(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteModelUseCase.invoke(id)
            fetchAllModels()
        }
    }
}