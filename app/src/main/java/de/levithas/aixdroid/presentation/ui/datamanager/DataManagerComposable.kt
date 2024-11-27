package de.levithas.aixdroid.presentation.ui.datamanager

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import de.levithas.aixdroid.presentation.ui.datamanager.dialog.DataManagerDialogComposable
import java.util.Date
import java.util.Locale

const val DATA_SET_LIST = 0
const val DATA_SERIES_LIST = 1
const val DATA_SET_DETAILS = 2
const val DATA_SET_CREATION = 3

@Composable
fun DataManagerComposable(
    modifier: Modifier = Modifier,
    viewModel: DataViewModel = hiltViewModel()
) {
    val dataSetList by viewModel.allDataSets.collectAsState()
    val allDataSeriesList by viewModel.allDataSeries.collectAsState() // TODO: Let database filter dataseries without dataset
    val markedDataSeriesList = viewModel.markedDataSeriesList

    val isImportingData by viewModel.isImporting.collectAsState()
    val importingState by viewModel.importProgress.collectAsState()
    val importDataMergeDecision by viewModel.importDataMergeDecision.collectAsState()

    var fileUri by remember { mutableStateOf(Uri.EMPTY) }
    val modelUriLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        fileUri = it
        if (fileUri != Uri.EMPTY) {
            viewModel.startDataSeriesImport(fileUri)
            fileUri = Uri.EMPTY
        }
    }

    var currentTab by rememberSaveable { mutableIntStateOf(DATA_SET_LIST) }

    var currentDataSeriesList by remember { mutableStateOf<List<DataSeries>>(listOf()) }

    var currentDataSetId by remember { mutableStateOf<Long?>(null) }


    DataManagerWindow(
        modifier = modifier,
        currentTab = currentTab,
        onBackToOverview = { currentTab = DATA_SET_LIST },

        dataSetList = dataSetList,
        dataSeriesList = allDataSeriesList,
        markedDataSeriesList = markedDataSeriesList,
        onToggleDataSeriesMark = { id -> viewModel.toggleMarkDataSeries(id) },

        onOpenDataSetDetails = { dataSet ->
            currentDataSetId = dataSet.id
            currentTab = DATA_SET_DETAILS
        },
        onOpenRemainingDataSeries = {},
        onOpenDataSetSeriesList = { dataSeriesList ->
            currentDataSeriesList = dataSeriesList
            currentTab = DATA_SERIES_LIST
        },

        onOpenDataImport = { modelUriLauncher.launch("text/*") },
        isImportingData = isImportingData, // Shows loading bar
        dataImportState = importingState, // State of loading bar
        onCancelImport = { viewModel.cancelDataImport() },
        currentDataSetId = currentDataSetId
    )

    if (importDataMergeDecision == ImportDataMergeDecision.ON_REQUEST) {
        DataManagerDialogComposable(
            modifier = Modifier,
            onDismiss = {
                viewModel.dataImportDecision(ImportDataMergeDecision.NO_MERGE)
            },
            onAccepted = {
                viewModel.dataImportDecision(ImportDataMergeDecision.MERGE)
            },
            dismissButtonText = "Abort",
            acceptButtonText = "Merge",
            message = "There are features with the same name as the new imported ones. Do you want to merge the data?"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagerWindow(
    modifier: Modifier,
    currentTab: Int,
    currentDataSetId: Long?,
    onBackToOverview: () -> Unit,
    dataSetList: List<DataSet>,
    dataSeriesList: List<DataSeries>,
    markedDataSeriesList: List<Long>,
    onToggleDataSeriesMark: (Long) -> Unit,
    onOpenDataSetDetails: (DataSet) -> Unit,
    onOpenRemainingDataSeries: () -> Unit,
    onOpenDataSetSeriesList: (List<DataSeries>) -> Unit,
    onOpenDataImport: () -> Unit,
    isImportingData: Boolean,
    dataImportState: Float,
    onCancelImport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.data_manager_title)) },
                colors = MaterialTheme.customColors.topAppBarColors,
                navigationIcon = {
                    if (currentTab > 0) {
                        IconButton(
                            onClick = onBackToOverview
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isImportingData) {
                DataSeriesImportLoading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 32.dp),
                    loadingState = dataImportState,
                    onCancelImport = onCancelImport
                )
            }

            when (currentTab) {
                DATA_SET_LIST -> DataListWindow(
                    modifier = modifier,
                    onOpenDataImport = onOpenDataImport
                ) {
                    DataSetList(
                        modifier = Modifier,
                        dataSetList = dataSetList,
                        dataSeriesWithoutDataSetList = dataSeriesList.filter { series ->
                            dataSetList.isEmpty() || dataSetList.none { dataSet ->
                                dataSet.columns.any { it.id == series.id }
                            }
                        },
                        onOpenDataSetDetails = onOpenDataSetDetails,
                        onOpenRemainingDataSeries = onOpenRemainingDataSeries,
                    )
                }

                DATA_SERIES_LIST -> DataListWindow(
                    modifier = Modifier,
                    onOpenDataImport = onOpenDataImport
                ) {
                    DataSeriesList(
                        modifier = modifier,
                        dataSeriesList = dataSeriesList,
                        markedDataSeriesList = markedDataSeriesList,
                        onToggleDataSeriesMark = onToggleDataSeriesMark
                    )
                }

                DATA_SET_DETAILS -> DataSetDetails(
                    modifier = modifier,
                    dataSet = dataSetList.find { it.id == currentDataSetId },
                    onOpenDataSetSeriesList = onOpenDataSetSeriesList,
                    onDeleteDataSeries = {},
                    onExportDataSeries = {},
                )

                DATA_SET_CREATION -> {}
            }
        }
    }
}

@Composable
fun DataListWindow(
    modifier: Modifier,
    onOpenDataImport: () -> Unit,
    dataListComposable: @Composable() () -> Unit
) {
    dataListComposable()

    IconButton(
        modifier = modifier
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
        Icon(Icons.Filled.Download, "Import", modifier = Modifier)
    }
}

@Composable
fun DataSetList(
    modifier: Modifier,
    dataSetList: List<DataSet>,
    dataSeriesWithoutDataSetList: List<DataSeries>,
    onOpenDataSetDetails: (DataSet) -> Unit,
    onOpenRemainingDataSeries: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (dataSeriesWithoutDataSetList.isNotEmpty()) {
            item {
                DataSetItem(
                    dataSet = DataSet(
                        null, stringResource(R.string.data_manager_not_designated_data_series), "",
                        columns = dataSeriesWithoutDataSetList,
                    ),
                    onOpenDataSetDetails = { _ -> onOpenRemainingDataSeries() },
                )
            }
        }

        items(dataSetList) { item ->
            DataSetItem(
                dataSet = item,
                onOpenDataSetDetails = onOpenDataSetDetails,
            )
        }
    }
}


@Composable
fun DataSeriesList(
    modifier: Modifier,
    dataSeriesList: List<DataSeries>,
    markedDataSeriesList: List<Long>,
    onToggleDataSeriesMark: (Long) -> Unit
) {
    if (dataSeriesList.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(dataSeriesList) { item ->
                DataSeriesItem(
                    dataSeriesItem = item,
                    showMarker = markedDataSeriesList.isNotEmpty(),
                    isMarked = markedDataSeriesList.contains(item.id),
                    onCheckedChanged = { _ -> onToggleDataSeriesMark(item.id!!) }
                )
            }
        }
    } else {
        Text("No Data Series available")
    }
}

@Composable
fun DataSeriesItem(
    modifier: Modifier = Modifier,
    dataSeriesItem: DataSeries,
    showMarker: Boolean,
    isMarked: Boolean = true,
    onCheckedChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(82.dp),
        colors = MaterialTheme.customColors.dataItemSelectedCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showMarker) {
                Checkbox(
                    checked = isMarked,
                    onCheckedChange = onCheckedChanged
                )
            }

            Card(
                modifier = if (!showMarker) modifier
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onCheckedChanged(true) },
                        )
                    } else modifier.fillMaxHeight(),
                colors = MaterialTheme.customColors.dataSeriesItemCard
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
    }
}

@Composable
fun DataSetItem(
    dataSet: DataSet,
    onOpenDataSetDetails: (DataSet) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 128.dp)
            .padding(8.dp)
            .clickable {
                onOpenDataSetDetails(dataSet)
            },
        colors = MaterialTheme.customColors.dataSetItemCard
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                    text = dataSet.name
                )
                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodyMedium,
                    text = "Data Columns: " + dataSet.columns.size
                )
            }
        }
    }
}


@Composable
fun DataSetDetails(
    modifier: Modifier,
    dataSet: DataSet?,
    onOpenDataSetSeriesList: (List<DataSeries>) -> Unit,
    onDeleteDataSeries: (Long) -> Unit,
    onExportDataSeries: (Long) -> Unit
) {
    dataSet?.let {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(AbsoluteAlignment.Left),
                    style = MaterialTheme.typography.titleLarge,
                    text = dataSet.name
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(AbsoluteAlignment.Left),
                    style = MaterialTheme.typography.titleMedium,
                    text = "Description:"
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(AbsoluteAlignment.Left),
                    style = MaterialTheme.typography.bodyMedium,
                    text = dataSet.description
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(style = MaterialTheme.typography.titleMedium, text = "Start Date:")
                    Spacer(Modifier.width(8.dp))
                    Text(text = Date(dataSet.columns.minOf { it.startTime?.time ?: 0 }).toString())
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(style = MaterialTheme.typography.titleMedium, text = "End Date:")
                    Spacer(Modifier.width(8.dp))
                    Text(text = Date(dataSet.columns.maxOf { it.endTime?.time ?: 0 }).toString())
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(style = MaterialTheme.typography.titleMedium, text = "Feature Count:")
                    Spacer(Modifier.width(8.dp))
                    Text(text = dataSet.columns.size.toString())
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(style = MaterialTheme.typography.titleMedium, text = "Total Data Count:")
                    Spacer(Modifier.width(8.dp))
                    Text(text = dataSet.columns.sumOf { it.count ?: 0 }.toString())
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    }
                ) {
                    Text("Predict with Model")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOpenDataSetSeriesList(dataSet.columns) }
                ) {
                    Text("Show Features")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    }
                ) {
                    Text("Dissolve Data Set")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    }
                ) {
                    Text("Export Data")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    }
                ) {
                    Text("Delete Data")
                }
            }
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
                text = "Data importing..." + String.format(locale = Locale.GERMAN, "%.2f", loadingState * 100) + "%"
            )
            Button(
                onClick = onCancelImport
            ) {
                Text("Cancel")
            }
        }
    }
}

val dataSeriesPreviewList = listOf(
    DataSeries(
        id = 0,
        origin = "CSV-Import",
        name = "Heartrate",
        unit = "bpm",
        count = 1234567,
        startTime = Date(1000000),
        endTime = Date(1010000)
    ),
    DataSeries(
        id = 1,
        origin = "CSV-Import",
        name = "Movement",
        unit = "max(m/s²)/min",
        count = 1234567,
        startTime = Date(1000000),
        endTime = Date(1010000)
    )
)

val dataSetPreviewItem = DataSet(
    id = 0,
    name = "Schlafdaten",
    description = "Dieser Datensatz enthält alle Daten, die von der BangleJS empfangen wurden.",
    columns = dataSeriesPreviewList.drop(1)
)

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun Preview() {
    AiXDroidTheme() {
        DataManagerWindow(
            modifier = Modifier,
            currentTab = 0,
            dataSetList = listOf(dataSetPreviewItem),
            onOpenDataImport = {},
            isImportingData = false,
            dataImportState = 0.0f,
            onCancelImport = {},
            onBackToOverview = {},
            dataSeriesList = dataSeriesPreviewList,
            markedDataSeriesList = listOf(),
            onToggleDataSeriesMark = {},
            currentDataSetId = 0,
            onOpenDataSetDetails = {},
            onOpenRemainingDataSeries = {},
            onOpenDataSetSeriesList = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun DataSeriesDetailPreview() {
    AiXDroidTheme {
        DataSetDetails(
            modifier = Modifier,
            dataSet = dataSetPreviewItem,
            onDeleteDataSeries = {},
            onExportDataSeries = {},
            onOpenDataSetSeriesList = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun DataImportDecisionDialogPreview() {
    AiXDroidTheme {
        DataManagerDialogComposable(
            modifier = Modifier,
            message = "Hast du lust auf Feierabend?",
            acceptButtonText = "Klar doch!",
            dismissButtonText = "Nö",
            onAccepted = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun DataSetItemPreview() {
    AiXDroidTheme {
        DataSetItem(
            dataSet = dataSetPreviewItem,
            onOpenDataSetDetails = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun DataSeriesItemPreview() {
    AiXDroidTheme {
        DataSeriesItem(
            modifier = Modifier,
            showMarker = true,
            isMarked = false,
            dataSeriesItem = DataSeries(
                id = 0,
                origin = "CSV-Import",
                name = "Heartrate",
                unit = "bpm",
                count = 10304032,
                startTime = Date(100000),
                endTime = Date(1000100)
            ),
            onCheckedChanged = {}
        )
    }
}