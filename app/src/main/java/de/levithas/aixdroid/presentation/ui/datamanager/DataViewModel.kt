package de.levithas.aixdroid.presentation.ui.datamanager

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.usecase.datamanager.DeleteDataSeriesUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataSeriesListUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ImportDataUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val getDataSeriesListUseCase: GetDataSeriesListUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val deleteDataSeriesUseCase: DeleteDataSeriesUseCase
    ) : ViewModel() {

    private val _allDataSeries = MutableStateFlow<List<DataSeries>>(emptyList())
    val allDataSeries: StateFlow<List<DataSeries>> get() = _allDataSeries

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> get() = _isImporting

    private val _importProgress = MutableStateFlow(0.0f)
    val importProgress: StateFlow<Float> get() = _importProgress


    private var importJob: Job? = null

    init {
        fetchAllDataSeries()
    }

    private fun fetchAllDataSeries() {
        viewModelScope.launch(Dispatchers.IO) {
            getDataSeriesListUseCase.invoke().collect { list ->
                _allDataSeries.value = list
            }
        }
    }

    fun importDataSeries(uri: Uri) {
        importJob = viewModelScope.launch(Dispatchers.IO) {
            _isImporting.value = true
            _importProgress.value = 0.0f

            try {
                importDataUseCase.invoke(applicationContext, uri) { progress ->
                    _importProgress.value = progress
                }
            } catch (e: CancellationException) {
                Log.w("Data Import", "Data Import was canceled: " + e.message)
            }
            finally {
                _isImporting.value = false
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