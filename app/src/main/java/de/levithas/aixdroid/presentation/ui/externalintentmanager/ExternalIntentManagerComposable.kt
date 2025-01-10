package de.levithas.aixdroid.presentation.ui.externalintentmanager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors

private const val TAB_OVERVIEW = 0
private const val TAB_DETAIL_VIEW = 1


@Composable
fun ExternalIntentManagerComposable(
    modifier: Modifier = Modifier,
    viewModel: ExternalIntentViewModel = hiltViewModel()
) {
    val intentDataList by viewModel.allIntentData.collectAsState()

    ExternalIntentManagerWindow(
        modifier = modifier,
        intentDataList = intentDataList,
        onCreateUpdateIntentData = { intentDataConfiguration -> viewModel.createUpdateIntentData(intentDataConfiguration) },
        onDeleteIntentData = { packageName -> viewModel.deleteIntentData(packageName) }
    )
}

@Composable
fun ExternalIntentManagerWindow(
    modifier: Modifier,
    intentDataList: List<ExternalIntentConfiguration>,
    onCreateUpdateIntentData: (ExternalIntentConfiguration) -> Unit,
    onDeleteIntentData: (String) -> Unit
) {
    var currentTab by rememberSaveable { mutableIntStateOf(TAB_OVERVIEW) }

    var currentIntentData by remember { mutableStateOf<ExternalIntentConfiguration?>(null)}

    when (currentTab) {
        TAB_OVERVIEW -> ExternalIntentOverview(
            modifier,
            intentDataList,
            onOpenIntentDataDetails = { intentData ->
                currentIntentData = intentData
                currentTab = TAB_DETAIL_VIEW
            }
        )
        TAB_DETAIL_VIEW -> ExternalIntentDetailView(
            intentData = currentIntentData,
            onBackToOverview = { currentTab = TAB_OVERVIEW },
            onCreateUpdateIntentData = { intentData ->
                currentIntentData = intentData
                onCreateUpdateIntentData(intentData)
                currentTab = TAB_OVERVIEW
            },
            onDeleteIntentData = { currentIntentData?.let {
                onDeleteIntentData(it.packageName)
                currentTab = TAB_OVERVIEW
            } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalIntentOverview(
    modifier: Modifier,
    intentDataList: List<ExternalIntentConfiguration>,
    onOpenIntentDataDetails: (ExternalIntentConfiguration?) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.external_intent_manager_title)) },
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
            ExternalIntentItemList(
                modifier = Modifier,
                intentDataList = intentDataList,
                onOpenIntentDataDetails = { intentData -> onOpenIntentDataDetails(intentData) }
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
                onClick = {
                    onOpenIntentDataDetails(null)
                }
            ) {
                Icon(Icons.Filled.Add , "Add" , modifier = Modifier)
            }
        }
    }
}

@Composable
fun ExternalIntentItemList(
    modifier: Modifier,
    intentDataList: List<ExternalIntentConfiguration>,
    onOpenIntentDataDetails: (ExternalIntentConfiguration?) -> Unit
) {
    Text("Willkommen im External Intent Manager!")

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(intentDataList) { item ->
            ExternalIntentItem(
                item,
                onClickItem = { onOpenIntentDataDetails(item) }
            )
        }
    }
}

@Composable
fun ExternalIntentItem(
    intentData: ExternalIntentConfiguration,
    onClickItem: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
            .padding(8.dp)
            .clickable {
                onClickItem()
            },
        colors = MaterialTheme.customColors.itemCard,
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
                    text = intentData.name
                )
                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodyMedium,
                    text = intentData.packageName
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalIntentDetailView(
    intentData: ExternalIntentConfiguration?,
    onBackToOverview: () -> Unit,
    onCreateUpdateIntentData: (ExternalIntentConfiguration) -> Unit,
    onDeleteIntentData: () -> Unit
) {
    var name by remember { mutableStateOf(intentData?.name?:"") }
    var packageName by remember {mutableStateOf(intentData?.packageName?:"")}
    var allowReadData by remember {mutableStateOf(intentData?.allowReadData?:false)}
    var allowWriteData by remember {mutableStateOf(intentData?.allowWriteData?:false)}
    var allowInference by remember {mutableStateOf(intentData?.allowInference?:false)}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.external_intent_manager_details)) },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allowWriteData,
                        onCheckedChange = { allowWriteData = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Allow Writing Data")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allowReadData,
                        onCheckedChange = { allowReadData = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Allow Reading Data")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allowInference,
                        onCheckedChange = { allowInference = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Allow Inference")
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        onClick = {
                            onCreateUpdateIntentData(
                                ExternalIntentConfiguration(
                                    name = name,
                                    packageName = packageName,
                                    allowReadData = allowReadData,
                                    allowWriteData = allowWriteData,
                                    allowInference = allowInference
                                )
                            )
                        }
                    ) {
                        Text(if (intentData != null) "Save" else "Create New")
                    }
                    intentData?.let {
                        Button(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            onClick = onDeleteIntentData
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

val intentDataPreviewItem = ExternalIntentConfiguration(
    name = "GadgetBridge",
    packageName = "package.path.thing.gadgetBridge",
    allowReadData = false,
    allowWriteData = true,
    allowInference = false
)


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun Preview() {
    AiXDroidTheme() { ExternalIntentManagerWindow(
        Modifier,
        emptyList(),
        {},
        {}
    ) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun ExternalIntentItemPreview() {
    AiXDroidTheme { ExternalIntentItem(
        intentDataPreviewItem,
        {}
    ) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun ExternalIntentDetailPreview() {
    AiXDroidTheme { ExternalIntentDetailView(
        intentDataPreviewItem,
        {},
        {},
        {}
    ) }
}
