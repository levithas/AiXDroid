package de.levithas.aixdroid.presentation.ui.modelmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.levithas.aixdroid.domain.usecase.AddNewAIModelUseCase
import kotlinx.coroutines.launch
import org.tensorflow.lite.schema.Metadata
import java.nio.file.Path
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val addNewAIModelUseCase: AddNewAIModelUseCase
) : ViewModel() {
    fun addModelConfiguration(name: String, path: Path, metadata: Metadata) {
        viewModelScope.launch {
            addNewAIModelUseCase(name, path, metadata)
        }
    }
}