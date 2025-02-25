package de.levithas.aixdroid.presentation.ui.datamanager

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.model.TensorData
import de.levithas.aixdroid.domain.usecase.aimodelmanager.InferenceDataUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataListsUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DataSetUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ImportDataMergeDecision {
    NONE,
    ON_REQUEST,
    MERGE,
    NO_MERGE
}


@HiltViewModel
class DataViewModel @Inject constructor(
    private val getDataListsUseCase: GetDataListsUseCase,
    private val dataSeriesUseCase: DataSeriesUseCase,
    private val dataSetUseCase: DataSetUseCase,
    private val inferenceDataUseCase: InferenceDataUseCase
) : ViewModel() {

    private val _allDataSets = MutableStateFlow<List<DataSet>>(emptyList())
    val allDataSets: StateFlow<List<DataSet>> get() = _allDataSets

    private val _allDataSeries = MutableStateFlow<List<DataSeries>>(emptyList())
    val allDataSeries: StateFlow<List<DataSeries>> get() = _allDataSeries

    private val _markedDataSeriesList = mutableStateListOf<DataSeries>()
    val markedDataSeriesList get() = _markedDataSeriesList

    private val _isInfering = MutableStateFlow(false)
    val isInfering: StateFlow<Boolean> get() = _isInfering

    private val _inferenceProgress = MutableStateFlow(0.0f)
    val inferenceProgress: StateFlow<Float> get() = _inferenceProgress

    fun clearMarkedDataSeriesList() {
        _markedDataSeriesList.clear()
    }

    fun toggleMarkDataSeries(dataSeries: DataSeries) {
        if (_markedDataSeriesList.contains(dataSeries)) {
            _markedDataSeriesList.remove(dataSeries)
        } else {
            _markedDataSeriesList.add(dataSeries)
        }
    }

    private var _currentDataSetId = 0L
    private val _currentDataSet = MutableStateFlow<DataSet?>(null)
    val currentDataSet: StateFlow<DataSet?> get() = _currentDataSet

    fun resetCurrentDataSet() {
        _currentDataSet.value = null
    }

    fun setCurrentDataSet(dataSetId: Long) {
        _currentDataSetId = dataSetId
        viewModelScope.launch(Dispatchers.IO) {
            _currentDataSet.value = dataSetUseCase.getDataSetById(dataSetId)?.firstOrNull()
//            dataSetUseCase.getDataSetById(_currentDataSetId)?.collect {
//                _currentDataSet.value = it
//            }
        }
    }

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> get() = _isImporting

    private val _importProgress = MutableStateFlow(0.0f)
    val importProgress: StateFlow<Float> get() = _importProgress

    private val _importDataMergeDecision = MutableStateFlow(ImportDataMergeDecision.NONE)
    val importDataMergeDecision: StateFlow<ImportDataMergeDecision> get() = _importDataMergeDecision

    private var importJob: Job? = null
    private var inferenceJob: Job? = null
    private var assignJob: Job? = null

    init {
        fetchDataSeriesList()
        fetchDataSetList()
    }

    private fun fetchDataSeriesList() {
        viewModelScope.launch(Dispatchers.IO) {
            getDataListsUseCase.getDataSeriesFlow().collect { list ->
                _allDataSeries.value = list
            }
        }
    }

    private fun fetchDataSetList() {
        viewModelScope.launch(Dispatchers.IO) {
            getDataListsUseCase.getDataSetsFlow().collect { list ->
                _allDataSets.value = list
            }
        }
    }

    fun dataImportDecision(decision: ImportDataMergeDecision) {
        _importDataMergeDecision.value = decision
    }

    fun startDataSeriesImport(uri: Uri) {
        importJob = viewModelScope.launch(Dispatchers.IO) {
            _isImporting.value = true
            _importProgress.value = 0.0f

            try {
                // TODO: For the moment, just merge...
//                if (dataSeriesUseCase.checkExistingDataSeriesNames(uri)) {
//                    _importDataMergeDecision.value = ImportDataMergeDecision.ON_REQUEST
//
//                    // Wait for UI to make a decision
//                    val decision = _importDataMergeDecision.filter { it != ImportDataMergeDecision.ON_REQUEST }.first()
//
//                    when (decision) {
//                        ImportDataMergeDecision.NONE -> return@launch
//                        ImportDataMergeDecision.ON_REQUEST -> {
//                            error("UI decision did not block!!!")
//                        }
//
//                        ImportDataMergeDecision.MERGE -> {}
//                        ImportDataMergeDecision.NO_MERGE -> return@launch
//                    }
//                }

                dataSeriesUseCase.importFromCSV(uri) { progress ->
                    _importProgress.value = progress
                }
            } catch (e: CancellationException) {
                Log.w("Data Import", "Data Import was canceled: " + e.message)
            } finally {
                _isImporting.value = false
                _importDataMergeDecision.value = ImportDataMergeDecision.NONE
            }
        }
    }

    fun cancelDataImport() {
        importJob?.cancel()
    }

    fun addPredictionDataSeriesToDataSet(dataSet: DataSet, dataSeries: DataSeries) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSeries.id = dataSeriesUseCase.addDataSeries(dataSeries)
            val dataSetCopy = dataSet.copy()
            dataSetCopy.predictionSeries = dataSeries
            dataSetUseCase.updateDataSet(dataSetCopy)
        }
    }

    fun createUpdateDataSeries(dataSeries: DataSeries) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dataSeries.id == null) {
                dataSeriesUseCase.addDataSeries(dataSeries)
            } else {
                dataSeriesUseCase.updateDataSeries(dataSeries)
            }
        }
    }

    fun createUpdateDataSet(dataSet: DataSet) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dataSet.id == null) {
                dataSetUseCase.createDataSet(dataSet)
            } else {
                dataSetUseCase.updateDataSet(dataSet)
            }
        }
    }

    fun assignTensorDataToDataSet(dataSet: DataSet, tensorDataMap: Map<Long, Long>) {
        assignJob = viewModelScope.launch(Dispatchers.IO) {
            dataSet.id?.let {
                dataSetUseCase.assignTensorDataList(it, tensorDataMap)
            }
        }
    }

    fun removeDataSeriesFromDataSet(dataSet: DataSet, dataSeriesList: List<DataSeries>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSetUseCase.removeDataSeriesFromDataSet(dataSet, dataSeriesList)
        }
    }

    fun dissolveDataSet(dataSetId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSetUseCase.dissolveDataSet(dataSetId)
        }
    }

    fun deleteDataSeries(dataSeriesId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSeriesUseCase.deleteDataSeries(dataSeriesId)
        }
    }

    fun deleteDataSeriesList(dataSeriesIdList: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSeriesIdList.forEach { dataSeriesUseCase.deleteDataSeries(it) }
        }
    }

    fun startInference() {
        inferenceJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                assignJob?.join()

                currentDataSet.value?.id?.let { setCurrentDataSet(it) }

                _isInfering.value = true
                _inferenceProgress.value = 0.0f

                currentDataSet.value?.let { dataSet ->
                    inferenceDataUseCase.startInference(dataSet) { progress ->
                        _inferenceProgress.value = progress
                    }
                }
            } catch (e: CancellationException) {
                Log.w("Data Inference", "Data inference was canceled: " + e.message)
            } finally {
                _isInfering.value = false
            }
        }
    }

    fun cancelInference() {
        inferenceJob?.cancel()
    }
}