package de.levithas.aixdroid.presentation.ui.modelmanager

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AIModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val aiModelUseCase: AIModelUseCase,
    private val getModelListUseCase: GetModelListUseCase,
) : ViewModel() {

    private val _allModels = MutableStateFlow<List<ModelData>>(emptyList())
    val allModels: StateFlow<List<ModelData>> get() = _allModels

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

    fun addDataModel(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            aiModelUseCase.addNewModel(applicationContext, uri)
        }
    }

    fun deleteDataModel(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            aiModelUseCase.deleteModel(applicationContext, fileName)
            fetchAllDataModels()
        }
    }
}