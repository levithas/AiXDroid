package de.levithas.aixdroid.presentation.ui.externalintentmanager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import de.levithas.aixdroid.presentation.ui.datamanager.DataManagerComposable
import de.levithas.aixdroid.presentation.ui.modelmanager.AIModelItem
import de.levithas.aixdroid.presentation.ui.modelmanager.AIModelItemList

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
        TAB_DETAIL_VIEW -> currentIntentData?.let { ExternalIntentDetailView(
            modifier = modifier,
            intentData = it,
            onBackToOverview = { currentTab = TAB_OVERVIEW },
            onCreateUpdateIntentData = { intentData ->
                currentIntentData = intentData
                onCreateUpdateIntentData(intentData)
            }
        )}
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
                intentDataList = intentDataList
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
) {
    Text("Willkommen im External Intent Manager!")

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(intentDataList) { item ->
            ExternalIntentItem(
                Modifier,
                item
            )
        }
    }
}

@Composable
fun ExternalIntentItem(
    modifier: Modifier,
    intentData: ExternalIntentConfiguration
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
            .padding(8.dp)
            .clickable {

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


@Composable
fun ExternalIntentDetailView(
    modifier: Modifier,
    intentData: ExternalIntentConfiguration,
    onBackToOverview: () -> Unit,
    onCreateUpdateIntentData: (ExternalIntentConfiguration) -> Unit
) {

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
        Modifier,
        intentDataPreviewItem
    ) }
}