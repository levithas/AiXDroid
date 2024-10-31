package de.levithas.aixdroid.presentation.ui.modelmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.levithas.aixdroid.R
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors
import org.tensorflow.lite.schema.Metadata
import kotlin.io.path.Path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelManagerComposable(
    modifier: Modifier = Modifier,
) {
    val viewModel: AIViewModel = hiltViewModel()

    val modelList by viewModel.allModels.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.model_manager_title)) },
                colors = MaterialTheme.customColors.topAppBarColors
            )
        }
    ) { paddingValues ->
        // Hauptinhalt des DataManagerScreens
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Willkommen im AI Model Manager!")

            LazyColumn {
                items(modelList) { item ->
                    Row {
                        Text(item.name)
                        Button(
                            onClick = {
                                viewModel.deleteModelConfiguration(item.id.toLong())
                            }
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }

            IconButton(
                modifier = modifier.defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
                colors = IconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.tertiary
                ),
                onClick = {
                    viewModel.addModelConfiguration("Test", Path("Bla"), Metadata())
                },
            ) {
                Icon(Icons.Filled.Add , "Add" , modifier = Modifier)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun Preview() {
    AiXDroidTheme { AIModelManagerComposable() }
}