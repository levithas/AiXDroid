package de.levithas.aixdroid.presentation.ui.externalintentmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.levithas.aixdroid.R
import de.levithas.aixdroid.presentation.theme.customColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalIntentManagerComposable(
    modifier: Modifier = Modifier
) {
    val viewModel: ExternalIntentViewModel by remember { mutableStateOf(ExternalIntentViewModel())}

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.external_intent_manager_title)) },
                colors = MaterialTheme.customColors.topAppBarColors
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("Willkommen im External Intent Manager")

        }
    }
}