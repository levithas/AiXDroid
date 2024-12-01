package de.levithas.aixdroid.presentation.ui.modelmanager

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AddNewAIModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.DeleteModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val addNewAIModelUseCase: AddNewAIModelUseCase,
    private val getModelListUseCase: GetModelListUseCase,
    private val deleteModelUseCase: DeleteModelUseCase,
    private val dataSeriesUseCase: DataSeriesUseCase
) : ViewModel() {

    private val _allModels = MutableStateFlow<List<ModelData>>(emptyList())
    val allModels: StateFlow<List<ModelData>> get() = _allModels

    private val _isInfering = MutableStateFlow(false)
    val isInfering: StateFlow<Boolean> get() = _isInfering

    private val _inferenceProgress = MutableStateFlow(0.0f)
    val inferenceProgress: StateFlow<Float> get() = _inferenceProgress

    init {
        fetchAllDataModels()
    }

    private fun fetchAllDataModels() {
        viewModelScope.launch(Dispatchers.IO) {
            getModelListUseCase.invoke().collect { list ->
                _allModels.value = list
            }
        }
    }

    fun startInference(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun addDataModel(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            addNewAIModelUseCase(applicationContext, uri)
        }
    }

    fun deleteDataModel(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteModelUseCase.invoke(uri)
            fetchAllDataModels()
        }
    }
}