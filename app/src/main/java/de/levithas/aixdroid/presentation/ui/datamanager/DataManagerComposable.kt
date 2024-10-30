package de.levithas.aixdroid.presentation.ui.datamanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.levithas.aixdroid.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagerComposable() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.data_manager_title)) },
                // Weitere AppBar-Elemente hier
            )
        }
    ) { paddingValues ->
        // Hauptinhalt des DataManagerScreens
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("Willkommen im Data Manager!")
            // Weitere UI-Elemente hier
        }
    }
}