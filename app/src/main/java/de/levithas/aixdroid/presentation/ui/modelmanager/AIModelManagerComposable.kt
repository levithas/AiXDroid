package de.levithas.aixdroid.presentation.ui.modelmanager

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.model.TensorData
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import de.levithas.aixdroid.utils.DisplayUtilities
import org.tensorflow.lite.schema.TensorType


private const val TAB_OVERVIEW = 0
private const val TAB_MODEL_DETAILS = 1

@Composable
fun AIModelManagerComposable(
    modifier: Modifier = Modifier,
    viewModel: AIViewModel = hiltViewModel()
) {
    val modelList by viewModel.allModels.collectAsState()

    AIModelWindow(
        modifier = modifier,
        modelList = modelList,
        onAddNewDataModel = {
            uri -> viewModel.addDataModel(uri)
                            },
        onDeleteDataModel = {
            uri -> viewModel.deleteDataModel(uri)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelWindow(
    modifier: Modifier,
    modelList: List<ModelData>,
    onAddNewDataModel: (Uri) -> Unit,
    onDeleteDataModel: (Uri) -> Unit,
) {
    var currentTab by rememberSaveable { mutableIntStateOf(TAB_OVERVIEW) }
    val onBackToOverview = { currentTab = TAB_OVERVIEW }

    var showDeleteItemDialog by remember { mutableStateOf(false) }

    var modelUri by remember { mutableStateOf(Uri.EMPTY)}
    val modelUriLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        modelUri = it
        if (modelUri != Uri.EMPTY) {
            onAddNewDataModel(modelUri)
            modelUri = Uri.EMPTY
        }
    }

    var modelData by remember { mutableStateOf(ModelData()) }

    when (currentTab) {
        TAB_OVERVIEW -> AIModelOverview(
            modifier = modifier,
            modelList = modelList,
            onOpenDetails = { item ->
                modelData = item
                currentTab = TAB_MODEL_DETAILS
            },
            onAddNewDataModelRequested =  {
                modelUriLauncher.launch("application/*")
            }
        )
        TAB_MODEL_DETAILS -> AIModelDetailScreen(
            modifier = modifier,
            onBackToOverview = onBackToOverview,
            modelData = modelData,
            onDeleteModelRequested = { showDeleteItemDialog = true }
        )
    }

    if (showDeleteItemDialog) {
        AIModelDeleteItemDialog(
            modifier = Modifier,
            modelUri = modelData.uri,
            onDismiss = { showDeleteItemDialog = false },
            onDeleteModel = { uri ->
                showDeleteItemDialog = false
                onBackToOverview()
                onDeleteDataModel(uri)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelOverview(
    modifier: Modifier,
    modelList: List<ModelData>,
    onAddNewDataModelRequested: () -> Unit,
    onOpenDetails: (ModelData) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.model_manager_title)) },
                colors = MaterialTheme.customColors.topAppBarColors
            )
        },
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            AIModelItemList(
                modifier = Modifier.weight(1f),
                modelList = modelList,
                onOpenDetails = onOpenDetails
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
                onClick = onAddNewDataModelRequested
            ) {
                Icon(Icons.Filled.Add , "Add" , modifier = Modifier)
            }
        }
    }
}

@Composable
fun AIModelItemList(
    modifier: Modifier,
    modelList: List<ModelData>,
    onOpenDetails: (ModelData) -> Unit
) {
    Text("Willkommen im AI Model Manager!")

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(modelList) { model ->
            AIModelItem(
                modifier = modifier,
                model = model,
                onOpenDetails = onOpenDetails
            )
        }
    }
}

@Composable
fun AIModelItem(
    modifier: Modifier,
    model: ModelData,
    onOpenDetails: (ModelData) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(128.dp)
            .clickable {
                onOpenDetails(model)
            }
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
                text = model.name
            )
            Text(
                modifier = Modifier,
                style = MaterialTheme.typography.bodyMedium,
                text = DisplayUtilities.shortenString(model.description, 100)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelDetailScreen(
    modifier: Modifier,
    modelData: ModelData,
    onBackToOverview: () -> Unit,
    onDeleteModelRequested: (Uri) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.model_manager_edit_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToOverview
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = MaterialTheme.customColors.topAppBarColors
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = modelData.name
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    text = modelData.description
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(modelData.inputs) { tensor ->
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(tensor.name)
                            Spacer(Modifier.width(8.dp))
                            Text(TensorType.name(tensor.type.toInt()))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(modelData.outputs) { tensor ->
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(tensor.name)
                            Spacer(Modifier.width(8.dp))
                            Text(TensorType.name(tensor.type.toInt()))
                        }
                    }
                }
            }

            Button(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                onClick = {
                    onDeleteModelRequested(modelData.uri)
                }
            ) {
                Text("Delete Model")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelDeleteItemDialog(
    modifier: Modifier,
    modelUri: Uri,
    onDeleteModel: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Do you really want to delete this model? It has nothing done to you!\n\nReceived Message from Model:\n'Pleaasse don't " +
                        "delete me! I will try to get better, I promise!' "
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { onDeleteModel(modelUri) }
                    ) {
                        Text("Confirm")
                    }
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}


val modelConfigurationPreview = ModelData(

    uri = Uri.parse("/pfad/zu/modell/TestModel.tflite"),

    name = "TestModel",
    description = "Hier steht eine echt tolle Beschreibung zu diesem Modell. Das hier ist nämlich die Ultimative KI, die die Weltherschaft an sich reißen wird!",
    version = "1.0.0",
    author = "Levithas",
    licence = "MIT Licence",
    inputs = arrayOf(
        TensorData(
            name = "Nachricht",
            description = "Enthält die Nachricht die du an das Modell schicken willst",
            type = TensorType.STRING,
            shape = arrayOf(10,2,300).toList(),
            min = 0.0f,
            max = 1.0f,
        ),
        TensorData(
            name = "Herzfrequenz",
            description = "Dein Herzschlag, damit das Modell weiß, dass du Angst hast!",
            type = TensorType.FLOAT32,
            shape = arrayOf(1,2,300).toList(),
            min = 0.0f,
            max = 200.0f,
        )
    ).toList(),
    outputs = arrayOf(
        TensorData(
            name = "Antwort",
            description = "Enthält die Nachricht des Modells an dich",
            type = TensorType.STRING,
            shape = arrayOf(10,2,300).toList(),
            min = 0.0f,
            max = 1.0f,
        ),
        TensorData(
            name = "Zerstörungsthreshold",
            description = "Der Wert gibt an, wie sehr das Modell die Welt vernichten möchte (0 = garnicht, 1 = sofort)",
            type = TensorType.FLOAT32,
            shape = arrayOf(1,2,300).toList(),
            min = 0.0f,
            max = 1.0f,
        )
    ).toList()
)

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun Preview() {
    AiXDroidTheme {
        AIModelWindow(
            modifier = Modifier,
            modelList = arrayListOf(
                modelConfigurationPreview,
            ),
            {},
            {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun AIModelItemPreview() {
    AiXDroidTheme { AIModelItem(Modifier, modelConfigurationPreview, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun AIModelEditScreenPreview() {
    AiXDroidTheme { AIModelDetailScreen(Modifier,
        modelData =modelConfigurationPreview,
        {},
        {}
    ) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun AIModelDeleteDialogPreview() {
    AIModelDeleteItemDialog(
        Modifier,
        modelConfigurationPreview.uri,
        {},
        {}
    )
}