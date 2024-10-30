package de.levithas.aixdroid.presentation.ui.modelmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.levithas.aixdroid.R
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.theme.customColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelManagerComposable(
    modifier: Modifier = Modifier
) {
    val viewModel: AIViewModel by remember { mutableStateOf(AIViewModel()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.model_manager_title)) },
                colors = MaterialTheme.customColors.topAppBarColors
            )
        }
    ) { paddingValues ->
        // Hauptinhalt des DataManagerScreens
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("Willkommen im AI Model Manager!")
            // Weitere UI-Elemente hier
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun Preview() {
    AiXDroidTheme { AIModelManagerComposable() }
}