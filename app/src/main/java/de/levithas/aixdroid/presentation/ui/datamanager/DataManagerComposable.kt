package de.levithas.aixdroid.presentation.ui.datamanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import java.util.Date


@Composable
fun DataManagerComposable(
    modifier: Modifier = Modifier,
    viewModel: DataViewModel = hiltViewModel()
) {

    val dataSeriesList by viewModel.allDataSeries.collectAsState()

    DataManagerWindow(
        modifier = modifier,
        dataSeriesList = dataSeriesList
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagerWindow(
    modifier: Modifier,
    dataSeriesList: List<DataSeries>
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
            modifier = modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
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
                onClick = { }
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
                text = "Datapoint Count: " + dataSeriesItem.data.size
            )
        }
    }
}

val dataSeriesPreviewList: List<DataSeries> = listOf(
    DataSeries(
        id = 0,
        name = "Test",
        unit = "m",
        data = listOf(
            DataPoint(
                id = 0,
                value = 1.0f,
                time = Date(0)
            ),
            DataPoint(
                id = 1,
                value = 0.2523423f,
                time = Date(100)
            ),
            DataPoint(
                id = 2,
                value = 1.5f,
                time = Date(250)
            )
        )
    ),
    DataSeries(
        id = 1,
        name = "Test",
        unit = "m",
        data = listOf(
            DataPoint(
                id = 4,
                value = 0.2523423f,
                time = Date(100)
            ),
            DataPoint(
                id = 5,
                value = 1.5f,
                time = Date(250)
            )
        )
    )
)

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun Preview() {
    AiXDroidTheme() { DataManagerWindow(
        modifier = Modifier,
        dataSeriesList = dataSeriesPreviewList
    ) }
}