package de.levithas.aixdroid.presentation.ui.datamanager

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.opticalCentering
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import java.util.Date
import java.util.Locale


@Composable
fun DataManagerComposable(
    modifier: Modifier = Modifier,
    viewModel: DataViewModel = hiltViewModel()
) {
    val dataSeriesList by viewModel.allDataSeries.collectAsState()
    val isImportingData by viewModel.isImporting.collectAsState()
    val importingState by viewModel.importProgress.collectAsState()

    var fileUri by remember { mutableStateOf(Uri.EMPTY)}

    val modelUriLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        fileUri = it
        if (fileUri != Uri.EMPTY) {
            viewModel.importDataSeries(fileUri)
            fileUri = Uri.EMPTY
        }
    }

    DataManagerWindow(
        modifier = modifier,
        dataSeriesList = dataSeriesList,
        onOpenDataImport = { modelUriLauncher.launch("text/*") },
        isImportingData = isImportingData, // Shows loading bar
        dataImportState = importingState, // State of loading bar
        onCancelImport = { viewModel.cancelDataImport() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagerWindow(
    modifier: Modifier,
    dataSeriesList: List<DataSeries>,
    onOpenDataImport: () -> Unit,
    isImportingData: Boolean,
    dataImportState: Float,
    onCancelImport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.data_manager_title)) },
                colors = MaterialTheme.customColors.topAppBarColors
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isImportingData) {
                DataSeriesImportLoading(
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 32.dp),
                    loadingState = dataImportState,
                    onCancelImport = onCancelImport
                )
            }


            Text("Willkommen im Data Manager!")



            DataSeriesOverview(
                modifier = Modifier.weight(1f),
                dataSeriesList = dataSeriesList
            )

            IconButton(
                modifier = modifier
                    .align(Alignment.End)
                    .padding(8.dp)
                    .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
                colors = IconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.tertiary
                ),
                onClick = onOpenDataImport
            ) {
                Icon(Icons.Filled.Download , "Import" , modifier = Modifier)
            }
        }
    }
}

@Composable
fun DataSeriesOverview(
    modifier: Modifier,
    dataSeriesList: List<DataSeries>
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(dataSeriesList) { item ->
            DataSeriesItem(
                dataSeriesItem = item
            )
        }
    }
}

@Composable
fun DataSeriesItem(
    modifier: Modifier = Modifier,
    dataSeriesItem: DataSeries
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(128.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                style = MaterialTheme.typography.titleMedium,
                text = dataSeriesItem.name
            )
            Text(
                modifier = Modifier,
                style = MaterialTheme.typography.bodyMedium,
                text = "Datapoint Count: " + dataSeriesItem.count
            )
        }
    }
}

@Composable
fun DataSeriesImportLoading(
    modifier: Modifier,
    loadingState: Float,
    onCancelImport: () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Data importing..." + String.format(locale = Locale.GERMAN,"%.2f", loadingState) + "%"
            )
            Button(
                onClick = onCancelImport
            ) {
                Text("Cancel")
            }
        }
    }
}

val dataSeriesPreviewList: List<DataSeries> = listOf(
    DataSeries(
        id = 0,
        name = "Test",
        unit = "m",
        count = 1110010,
        startTime = Date(10203404),
        endTime = Date(102034545)
    ),
    DataSeries(
        id = 1,
        name = "Test",
        unit = "m",
        count = 1005220,
        startTime = Date(10203404),
        endTime = Date(16203404),
    )
)

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun Preview() {
    AiXDroidTheme() { DataManagerWindow(
        modifier = Modifier,
        dataSeriesList = dataSeriesPreviewList,
        {},
        true,
        0.0f,
        {}
    ) }
}