package de.levithas.aixdroid.presentation.ui.modelmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import org.tensorflow.lite.schema.Metadata
import java.nio.file.Path
import kotlin.io.path.Path

const val TAB_OVERVIEW = 0
const val TAB_MODEL_DETAILS = 1

@Composable
fun AIModelManagerComposable(
    modifier: Modifier = Modifier,
    viewModel: AIViewModel = hiltViewModel()
) {
    val modelList by viewModel.allModels.collectAsState()

    AIModelWindow(
        modifier = modifier,
        modelList = modelList,
        onAddNewModel = { viewModel.addModelConfiguration(Path("")) },
        onDeleteItem = { path -> viewModel.deleteModelConfiguration(path) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelWindow(
    modifier: Modifier,
    modelList: List<ModelData>,
    onAddNewModel: () -> Unit,
    onDeleteItem: (Path) -> Unit,
) {
    var currentTab by rememberSaveable { mutableIntStateOf(TAB_OVERVIEW) }
    val onBackButtonPressed = { currentTab = TAB_OVERVIEW }

    val switchToNewModelScreen = {}
    var showDeleteItemDialog = false

    var modelData by rememberSaveable { mutableStateOf(ModelData(Path(""), "", "")) }

    when (currentTab) {
        TAB_OVERVIEW -> AIModelOverview(
            modifier = modifier,
            modelList = modelList,
            onAddNewModelPressed = switchToNewModelScreen,
            onDeleteItem = { showDeleteItemDialog = true }
        )
        TAB_MODEL_DETAILS -> AIModelDetailScreen(
            modifier = modifier,
            onAddNewModel = onAddNewModel,
            onBackButtonPressed = onBackButtonPressed,
            modelData = modelData
        )
    }

    AIModelDeleteItemDialog(
        modifier = Modifier,
        modelData = modelData,
        onDismiss = { showDeleteItemDialog = false },
        showDialog = showDeleteItemDialog,
        onDeleteItem = onDeleteItem
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelOverview(
    modifier: Modifier,
    modelList: List<ModelData>,
    onAddNewModelPressed: () -> Unit,
    onDeleteItem: (Path) -> Unit
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
                onDeleteItemPressed = onDeleteItem
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
                onClick = onAddNewModelPressed
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
    onDeleteItemPressed: (Path) -> Unit
) {
    Text("Willkommen im AI Model Manager!")

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(modelList) { item ->
            AIModelItem(
                modifier = modifier,
                item = item,
                onDeleteItemPressed = { onDeleteItemPressed(item.path) }
            )
        }
    }
}

@Composable
fun AIModelItem(
    modifier: Modifier,
    item: ModelData,
    onDeleteItemPressed: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = item.name
            )
            Button(
                modifier = Modifier,
                onClick = onDeleteItemPressed
            ) {
                Text("Remove")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelDetailScreen(
    modifier: Modifier,
    onBackButtonPressed: () -> Unit,
    modelData: ModelData
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.model_manager_edit_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackButtonPressed
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = MaterialTheme.customColors.topAppBarColors
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelDeleteItemDialog(
    modifier: Modifier,
    modelData: ModelData,
    showDialog: Boolean,
    onDeleteItem: (Path) -> Unit,
    onDismiss: () -> Unit
) {
    if(showDialog) {
        BasicAlertDialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Do you really want to delete this model? It has nothing done to you!")
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { onDeleteItem(modelData.path) }
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
}


val modelConfigurationPreview = ModelData(0, "Test", Path("Path"), Metadata())

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
    AiXDroidTheme { AIModelDetailScreen(Modifier, {}, {},
        modelData =modelConfigurationPreview
    ) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun AIModelDeleteDialogPreview() {
    AIModelDeleteItemDialog(
        Modifier,
        modelConfigurationPreview,
        true,
        {}
    ) { }
}