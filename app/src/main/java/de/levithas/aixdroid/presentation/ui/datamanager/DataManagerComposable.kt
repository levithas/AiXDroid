package de.levithas.aixdroid.presentation.ui.datamanager

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.collection.emptyObjectList
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.model.TensorData
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import de.levithas.aixdroid.presentation.ui.datamanager.dialog.DataManagerDialogComposable
import de.levithas.aixdroid.presentation.ui.modelmanager.AIModelItem
import de.levithas.aixdroid.presentation.ui.modelmanager.AIModelItemList
import de.levithas.aixdroid.presentation.ui.modelmanager.AIViewModel
import org.tensorflow.lite.schema.TensorType
import java.util.Date
import java.util.Dictionary
import java.util.Locale

const val DATA_SET_LIST = 0
const val DATA_SERIES_LIST = 1
const val DATA_SET_DETAILS = 2
const val DATA_SET_CREATION = 3
const val DATA_SET_INFERENCE = 4

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
    val currentDataSet by viewModel.currentDataSet.collectAsState()


    DataManagerWindow(
        modifier = modifier,
        currentTab = currentTab,
        onBackToOverview = {
            when (currentTab) {
                DATA_SET_DETAILS -> {
                    viewModel.resetCurrentDataSet()
                    currentTab = DATA_SET_LIST
                }
                DATA_SET_CREATION -> {
                    currentTab = if (currentDataSet == null) {
                        DATA_SET_LIST
                    } else {
                        DATA_SET_DETAILS
                    }
                }
                DATA_SERIES_LIST -> {
                    currentTab = if (currentDataSet == null) {
                        DATA_SET_LIST
                    } else {
                        DATA_SET_DETAILS
                    }
                }
                DATA_SET_INFERENCE -> {
                    currentTab = DATA_SET_DETAILS
                }
            }
        },

        dataSetList = dataSetList,
        dataSeriesList = allDataSeriesList,
        currentDataSeriesList = currentDataSeriesList,
        markedDataSeriesList = markedDataSeriesList,
        currentDataSet = currentDataSet,
        onToggleDataSeriesMark = { id -> viewModel.toggleMarkDataSeries(id) },

        onOpenDataSetDetails = { dataSetId ->
            viewModel.setCurrentDataSet(dataSetId)
            currentTab = DATA_SET_DETAILS
        },
        onOpenRemainingDataSeries = { dataSeriesList ->
            currentDataSeriesList = dataSeriesList
            currentTab = DATA_SERIES_LIST
        },
        onOpenDataSetSeriesList = { dataSeriesList ->
            currentDataSeriesList = dataSeriesList
            currentTab = DATA_SERIES_LIST
        },
        onJoinToDataSet = {
            currentDataSeriesList = markedDataSeriesList
            currentTab = DATA_SET_CREATION
        },


        onOpenDataImport = { modelUriLauncher.launch("text/*") },
        isImportingData = isImportingData, // Shows loading bar
        dataImportState = importingState, // State of loading bar
        onCancelImport = { viewModel.cancelDataImport() },


        onSaveDataSet = { adaptedDataSet ->
            viewModel.createUpdateDataSet(adaptedDataSet)
            viewModel.clearMarkedDataSeriesList()
            currentTab = DATA_SET_LIST
        },

        onDissolveDataSet = { currentDataSet?.id?.let {
            viewModel.dissolveDataSet(it)
            viewModel.resetCurrentDataSet()
            currentTab = DATA_SET_LIST
        } },
        onEditDataSet = {
            currentDataSeriesList = emptyList()
            currentTab = DATA_SET_CREATION
        },
        onRemoveDataSeriesFromDataSet = {
            currentDataSet?.let { viewModel.removeDataSeriesFromDataSet(it, currentDataSeriesList) }
            currentTab = DATA_SET_DETAILS
        },
        onOpenInferenceConfiguration = {
            currentTab = DATA_SET_INFERENCE
        },
        onSaveInferenceConfiguration = { dataSet, tensorMap ->
            currentDataSet?.let {
                viewModel.createUpdateDataSet(dataSet)
                viewModel.assignTensorDataToDataSet(it, tensorMap)
                currentTab = DATA_SET_DETAILS
            }
        }
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
    currentDataSet: DataSet?,
    currentDataSeriesList: List<DataSeries>,
    onBackToOverview: () -> Unit,
    dataSetList: List<DataSet>,
    dataSeriesList: List<DataSeries>,
    markedDataSeriesList: List<DataSeries>,
    onToggleDataSeriesMark: (DataSeries) -> Unit,
    onJoinToDataSet: () -> Unit,
    onOpenDataSetDetails: (Long) -> Unit,
    onEditDataSet: () -> Unit,
    onOpenRemainingDataSeries: (List<DataSeries>) -> Unit,
    onOpenDataSetSeriesList: (List<DataSeries>) -> Unit,
    onOpenDataImport: () -> Unit,
    onOpenInferenceConfiguration: () -> Unit,
    isImportingData: Boolean,
    dataImportState: Float,
    onCancelImport: () -> Unit,
    onSaveDataSet: (DataSet) -> Unit,
    onDissolveDataSet: () -> Unit,
    onRemoveDataSeriesFromDataSet: () -> Unit,
    onSaveInferenceConfiguration: (DataSet, Map<Long, Long>) -> Unit
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
                    onOpenDataImport = onOpenDataImport,
                    currentDataSet = currentDataSet,
                    onJoinToDataSet = {},
                    showJoinButton = false,
                    onRemoveDataSeriesFromDataSet = {}
                ) {
                    DataSetList(
                        modifier = Modifier,
                        dataSetList = dataSetList,
                        dataSeriesWithoutDataSetList = dataSeriesList.filter { series ->
                            dataSetList.isEmpty() || dataSetList.none { dataSet ->
                                dataSet.columns.any { it.key.id == series.id }
                            }
                        },
                        onOpenDataSetDetails = onOpenDataSetDetails,
                        onOpenRemainingDataSeries = onOpenRemainingDataSeries,
                    )
                }

                DATA_SERIES_LIST -> DataListWindow(
                    modifier = Modifier,
                    currentDataSet = currentDataSet,
                    onOpenDataImport = onOpenDataImport,
                    showJoinButton = markedDataSeriesList.isNotEmpty(),
                    onJoinToDataSet = onJoinToDataSet,
                    onRemoveDataSeriesFromDataSet = onRemoveDataSeriesFromDataSet
                ) {
                    DataSeriesList(
                        modifier = modifier,
                        dataSeriesList = currentDataSeriesList,
                        markedDataSeriesList = markedDataSeriesList,
                        onToggleDataSeriesMark = onToggleDataSeriesMark,
                    )
                }

                DATA_SET_DETAILS -> DataSetDetails(
                    dataSet = currentDataSet,
                    onOpenDataSetSeriesList = onOpenDataSetSeriesList,
                    onDissolveDataSet = onDissolveDataSet,
                    onDeleteDataSeries = {},
                    onExportDataSeries = {},
                    onEditDataSet = onEditDataSet,
                    onOpenInferenceConfiguration = onOpenInferenceConfiguration
                )

                DATA_SET_CREATION -> DataSetCreation(
                    modifier = modifier,
                    currentDataSet = currentDataSet,
                    currentDataSeriesList = currentDataSeriesList,
                    onSaveChanges = onSaveDataSet
                )

                DATA_SET_INFERENCE -> DataSetInferenceConfiguration(
                    modifier = modifier,
                    dataSet = currentDataSet
                ) { dataSet, tensorList ->
                    onSaveInferenceConfiguration(dataSet, tensorList)
                }
            }
        }
    }
}

@Composable
fun DataListWindow(
    modifier: Modifier,
    currentDataSet: DataSet?,
    onOpenDataImport: () -> Unit,
    showJoinButton: Boolean,
    onJoinToDataSet: () -> Unit,
    onRemoveDataSeriesFromDataSet: () -> Unit,
    dataListComposable: @Composable() () -> Unit
) {
    dataListComposable()

    if (showJoinButton) {
        if(currentDataSet != null) {
            IconButton(
                modifier = modifier
                    .padding(8.dp)
                    .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
                colors = IconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.tertiary
                ),
                onClick = onRemoveDataSeriesFromDataSet
            ) {
                Icon(Icons.Filled.Remove, "Remove", modifier = Modifier)
            }
        } else {
            IconButton(
                modifier = modifier
                    .padding(8.dp)
                    .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
                colors = IconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.tertiary
                ),
                onClick = onJoinToDataSet
            ) {
                Icon(Icons.Filled.Merge, "Join", modifier = Modifier)
            }    
        }
    } else {
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
}

@Composable
fun DataSetList(
    modifier: Modifier,
    dataSetList: List<DataSet>,
    dataSeriesWithoutDataSetList: List<DataSeries>,
    onOpenDataSetDetails: (Long) -> Unit,
    onOpenRemainingDataSeries: (List<DataSeries>) -> Unit,
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
                        columns = dataSeriesWithoutDataSetList.associateBy(keySelector = { it }, valueTransform = { null }),
                        aiModel = null,
                        autoPredict = false
                    ),
                    onOpenDataSetDetails = { onOpenRemainingDataSeries(dataSeriesWithoutDataSetList) },
                )
            }
        }

        items(dataSetList) { item ->
            DataSetItem(
                dataSet = item,
                onOpenDataSetDetails = { item.id?.let { onOpenDataSetDetails(it) } },
            )
        }
    }
}


@Composable
fun DataSeriesList(
    modifier: Modifier,
    dataSeriesList: List<DataSeries>,
    markedDataSeriesList: List<DataSeries>,
    onToggleDataSeriesMark: (DataSeries) -> Unit
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
                    isMarked = markedDataSeriesList.contains(item),
                    onCheckedChanged = { _ -> onToggleDataSeriesMark(item) }
                )
            }
        }
    } else {
        Text(
            modifier = modifier,
            text = "No Data Series"
        )
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
    onOpenDataSetDetails: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 128.dp)
            .padding(8.dp)
            .clickable {
                onOpenDataSetDetails()
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
fun DataSetCreation(
    modifier: Modifier,
    currentDataSet: DataSet?,
    currentDataSeriesList: List<DataSeries>,
    onSaveChanges: (DataSet) -> Unit,
) {
    var name by remember { mutableStateOf(currentDataSet?.name?:"")}
    var description by remember { mutableStateOf(currentDataSet?.description?:"")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(

        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            DataSeriesList(
                modifier = Modifier.padding(8.dp),
                dataSeriesList = (currentDataSet?.columns?.keys?.toList()?: emptyList()) + (currentDataSeriesList),
                markedDataSeriesList = emptyList(),
                onToggleDataSeriesMark = {}
            )
        }

        Button(
            modifier = modifier,
            onClick = {
                onSaveChanges(
                    DataSet(
                        id = currentDataSet?.id,
                        name = name,
                        description = description,
                        columns = (currentDataSet?.columns?: emptyMap()) + currentDataSeriesList.associateBy(keySelector = { it }, valueTransform = { null }),
                        aiModel = null,
                        autoPredict = false
                    )
                )
            },
        ) {
            Text("Save")
        }
    }
}

@Composable
fun DataSetDetails(
    dataSet: DataSet?,
    onOpenDataSetSeriesList: (List<DataSeries>) -> Unit,
    onEditDataSet: () -> Unit,
    onDissolveDataSet: () -> Unit,
    onOpenInferenceConfiguration: () -> Unit,
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
                    Text(text = Date(dataSet.columns.keys.minOf { it.startTime?.time ?: 0 }).toString())
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(style = MaterialTheme.typography.titleMedium, text = "End Date:")
                    Spacer(Modifier.width(8.dp))
                    Text(text = Date(dataSet.columns.keys.maxOf { it.endTime?.time ?: 0 }).toString())
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
                    Text(text = dataSet.columns.keys.sumOf { it.count ?: 0 }.toString())
                }
                dataSet.aiModel?.let {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                        ) {
                            Text(style = MaterialTheme.typography.titleMedium, text = "AI-Model: ")
                            Spacer(Modifier.width(8.dp))
                            Text(text = it.name)
                        }

                        // List of connected features
                        LazyColumn {
                            item {
                                Row(Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                                    TableCell(text = "InputTensor", weight = .5f)
                                    TableCell(text = "DataSeries", weight = .5f)
                                }
                            }

                            items(dataSet.columns.keys.toList()) { dataSeries ->
                                dataSet.columns[dataSeries]?.let {
                                    Row(Modifier.fillMaxWidth()) {
                                        TableCell(text = it.name, weight = .5f)
                                        TableCell(text = dataSeries.name, weight = .5f)
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                        ) {
                            Text(style = MaterialTheme.typography.titleMedium, text = "Auto-Prediction: ")
                            Spacer(Modifier.width(8.dp))
                            Text(text = if (dataSet.autoPredict) "Activated" else "Deactivated")
                        }
                    }
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
                    onClick = onOpenInferenceConfiguration
                ) {
                    Text("Configure Inference")
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEditDataSet
                ) {
                    Text("Edit Data Set")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOpenDataSetSeriesList(dataSet.columns.keys.toList()) }
                ) {
                    Text("Show Features")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDissolveDataSet
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
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier.border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun DataSetInferenceSelectModel(
    modifier: Modifier,
    modelList: List<ModelData>,
    onModelSelected: (ModelData) -> Unit,
) {
    // A List of all available Models
    // Click on a Model selects the model
    AIModelItemList(
        modifier = modifier,
        modelList = modelList,
        onOpenDetails = onModelSelected
    )
}

@Composable
fun DataSetInferenceFeatureItem(
    dataSeriesItem: DataSeries,
    onSelectDataSeries: (Long) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
            .padding(8.dp)
            .clickable {
                dataSeriesItem.id?.let { onSelectDataSeries(it) }
            },
        colors = MaterialTheme.customColors.dataSetItemCard,
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
                    text = dataSeriesItem.name
                )
                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodyMedium,
                    text = "Data Size: " + dataSeriesItem.count
                )
            }
        }
    }
}

@Composable
fun DataSetInferenceSelectFeatures(
    modifier: Modifier,
    currentModelTensor: TensorData,
    featureList: List<DataSeries>,
    onDataSeriesSelected: (Long) -> Unit
) {
    Column(
        modifier = modifier
    ) {

        Text(
            modifier = Modifier,
            style = MaterialTheme.typography.titleMedium,
            text = "Select a dataSeries for this feature:"
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
            TableCell(text = "Feature", weight = .4f)
            TableCell(text = "Datatype", weight = .6f)
        }
        Row(Modifier.fillMaxWidth()) {
            TableCell(text = currentModelTensor.name, weight = .4f)
            TableCell(text = TensorType.name(currentModelTensor.type.toInt()), weight = .6f)
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(featureList) { feature ->
                DataSetInferenceFeatureItem(dataSeriesItem = feature) { selectedDataSeries ->
                    onDataSeriesSelected(selectedDataSeries)
                }
            }
        }
    }

    // A List of all Features of the dataset
    // Click on the feature selects it for the current Model Input Number
}

@Composable
fun DataSetInferenceDefinePredictionConfiguration(
    modifier: Modifier = Modifier,
    onSaveConfiguration: (Boolean, DataSeries) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var enableAutoPrediction by remember { mutableStateOf(false) }

    val isSaveEnabled = name.isNotBlank() && unit.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input for DataSeries name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Input for unit
        OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("Unit") },
            modifier = Modifier.fillMaxWidth()
        )

        // Checkbox for Enable Auto Prediction
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = enableAutoPrediction,
                onCheckedChange = { enableAutoPrediction = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Auto Prediction on Changes")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save button
        Button(
            onClick = {
                if (isSaveEnabled) {
                    val dataSeries = DataSeries(
                        id = null,
                        origin = "Predicted",
                        name = name,
                        unit = unit,
                        count = null,
                        startTime = null,
                        endTime = null
                    )
                    onSaveConfiguration(enableAutoPrediction, dataSeries)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSaveEnabled
        ) {
            Text("Save Configuration")
        }
    }
}

const val INFERENCE_CONFIGURATION_SELECT_MODEL = 0
const val INFERENCE_CONFIGURATION_SELECT_FEATURES = 1
const val INFERENCE_CONFIGURATION_FINISH_CONFIG = 2

@Composable
fun DataSetInferenceConfiguration(
    modelViewModel: AIViewModel = hiltViewModel(),
    modifier: Modifier,
    dataSet: DataSet?,
    onSaveConfiguration: (DataSet, Map<Long, Long>) -> Unit
) {
    dataSet?.let {
        val aiModelList by modelViewModel.allModels.collectAsState()
        var currentStep by remember { mutableStateOf(INFERENCE_CONFIGURATION_SELECT_MODEL) }

        var selectedModel by remember { mutableStateOf(ModelData()) }
        var selectedFeatureList by rememberSaveable { mutableStateOf<Map<Long, Long>>(emptyMap()) }

        when (currentStep) {
            INFERENCE_CONFIGURATION_SELECT_MODEL -> DataSetInferenceSelectModel(
                modifier = modifier,
                modelList = aiModelList.filter { model -> dataSet.columns.size >= model.inputs.size },
            ) { modelData ->
                selectedModel = modelData
                currentStep = INFERENCE_CONFIGURATION_SELECT_FEATURES
            }
            INFERENCE_CONFIGURATION_SELECT_FEATURES -> DataSetInferenceSelectFeatures(
                modifier = modifier,
                featureList = dataSet.columns.keys.toList().filter { feature -> selectedFeatureList[feature.id] == null },
                currentModelTensor = selectedModel.inputs[selectedFeatureList.size]
            ) { dataSeriesId ->
                selectedModel.inputs[selectedFeatureList.size].id?.let {
                    selectedFeatureList = mapOf(Pair(it, dataSeriesId)) + selectedFeatureList
                }
                
                if (selectedFeatureList.size == selectedModel.inputs.size) {
                    currentStep = INFERENCE_CONFIGURATION_FINISH_CONFIG
                }
            }
            INFERENCE_CONFIGURATION_FINISH_CONFIG -> DataSetInferenceDefinePredictionConfiguration(
                modifier = modifier,
            ) { autoPredict, dataSeries ->
                dataSet.aiModel = selectedModel
                dataSet.autoPredict = autoPredict

                onSaveConfiguration(dataSet, selectedFeatureList)
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
    columns = dataSeriesPreviewList.drop(1).associateBy(keySelector = { it }, valueTransform = { null }),
    aiModel = ModelData(
        uri = Uri.parse("Blablabla/Pfad"),
        name = "Dolles Modell",
    ),
    autoPredict = false
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
            currentDataSet = dataSetPreviewItem,
            onOpenDataSetDetails = {},
            onOpenRemainingDataSeries = {},
            onOpenDataSetSeriesList = {},
            onJoinToDataSet = {},
            currentDataSeriesList = emptyList(),
            onSaveDataSet = {},
            onDissolveDataSet = {},
            onEditDataSet = {},
            onRemoveDataSeriesFromDataSet = {},
            onOpenInferenceConfiguration = {},
            onSaveInferenceConfiguration = {_, _ -> }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun DataSeriesDetailPreview() {
    AiXDroidTheme {
        DataSetDetails(
            dataSet = dataSetPreviewItem,
            onDeleteDataSeries = {},
            onExportDataSeries = {},
            onOpenDataSetSeriesList = {},
            onDissolveDataSet = {},
            onEditDataSet = {},
            onOpenInferenceConfiguration = {}
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

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun DataSetCreationPreview() {
    AiXDroidTheme {
        DataSetCreation(
            modifier = Modifier,
            currentDataSet = dataSetPreviewItem,
            currentDataSeriesList = emptyList(),
            onSaveChanges = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun DataSetInferenceConfigurationPreview() {
    AiXDroidTheme {
        DataSetInferenceSelectFeatures(
            modifier = Modifier,
            currentModelTensor = TensorData(
                id = 0,
                name = "heartrate",
                description = "Die Rate des herzes und sowas keine Ahnung",
                type = TensorType.FLOAT32,
                shape = listOf(1, 5),
                min = 0.0f,
                max = 1.0f
            ),
            featureList = dataSeriesPreviewList,
            onDataSeriesSelected = {},
        )
    }
}