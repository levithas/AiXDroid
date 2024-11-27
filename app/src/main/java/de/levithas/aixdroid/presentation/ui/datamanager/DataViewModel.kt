package de.levithas.aixdroid.presentation.ui.datamanager

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.usecase.datamanager.DeleteDataSeriesUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataListsUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ImportDataUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

enum class ImportDataMergeDecision {
    NONE,
    ON_REQUEST,
    MERGE,
    NO_MERGE
}


@HiltViewModel
class DataViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val getDataListsUseCase: GetDataListsUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val deleteDataSeriesUseCase: DeleteDataSeriesUseCase
) : ViewModel() {

    private val _allDataSets = MutableStateFlow<List<DataSet>>(emptyList())
    val allDataSets: StateFlow<List<DataSet>> get() = _allDataSets

    private val _allDataSeries = MutableStateFlow<List<DataSeries>>(emptyList())
    val allDataSeries: StateFlow<List<DataSeries>> get() = _allDataSeries



    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> get() = _isImporting

    private val _importProgress = MutableStateFlow(0.0f)
    val importProgress: StateFlow<Float> get() = _importProgress

    private val _importDataMergeDecision = MutableStateFlow(ImportDataMergeDecision.NONE)
    val importDataMergeDecision: StateFlow<ImportDataMergeDecision> get() = _importDataMergeDecision


    private var importJob: Job? = null

    init {
        fetchAllData()
    }

    private fun fetchAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            getDataListsUseCase.getDataSeriesFlow().collect { list ->
                _allDataSeries.value = list
            }
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
                if (importDataUseCase.checkExistingDataSeriesNames(applicationContext, uri)) {
                    _importDataMergeDecision.value = ImportDataMergeDecision.ON_REQUEST

                    // Wait for UI to make a decision
                    val decision = _importDataMergeDecision.filter { it != ImportDataMergeDecision.ON_REQUEST }.first()

                    when (decision) {
                        ImportDataMergeDecision.NONE -> return@launch
                        ImportDataMergeDecision.ON_REQUEST -> {
                            error("UI decision did not block!!!")
                        }

                        ImportDataMergeDecision.MERGE -> {}
                        ImportDataMergeDecision.NO_MERGE -> return@launch
                    }
                }

                importDataUseCase.invoke(applicationContext, uri) { progress ->
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

    fun deleteDataSeries(dataSeriesId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteDataSeriesUseCase.invoke(dataSeriesId)
        }
    }
}