package de.levithas.aixdroid.presentation.ui.externalintentmanager

import android.view.Display
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import de.levithas.aixdroid.domain.usecase.intentmanager.IntentDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExternalIntentViewModel @Inject constructor(
    private val intentDataUseCase: IntentDataUseCase
) : ViewModel() {

    private val _allIntendData = MutableStateFlow<List<ExternalIntentConfiguration>>(emptyList())
    val allIntentData: StateFlow<List<ExternalIntentConfiguration>> get() = _allIntendData

    private var intentWatcherJob: Job? = null
    val isIntentWatching: Boolean get() = intentWatcherJob?.isActive?:false

    init {
        fetchAllIntentData()
    }

    private fun fetchAllIntentData() {
        viewModelScope.launch(Dispatchers.IO) {
            intentDataUseCase.getAllExternalIntentConfigurations().collect { list ->
                _allIntendData.value = list
            }
        }
    }

    fun createUpdateIntentData(externalIntentConfiguration: ExternalIntentConfiguration) {
        viewModelScope.launch(Dispatchers.IO) {
            intentDataUseCase.createUpdateExternalIntentConfiguration(
                externalIntentConfiguration
            )
        }
    }

    fun deleteIntentData(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            intentDataUseCase.deleteExternalIntentConfiguration(packageName)
        }
    }
}