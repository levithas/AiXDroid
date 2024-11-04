package de.levithas.aixdroid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.ui.datamanager.DataManagerComposable
import de.levithas.aixdroid.presentation.ui.externalintentmanager.ExternalIntentManagerComposable
import de.levithas.aixdroid.presentation.ui.modelmanager.AIModelManagerComposable


const val TAB_EXTERNAL_INTENT_MANAGER = 0
const val TAB_DATA_MANAGER = 1
const val TAB_MODEL_MANAGER = 2

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiXDroidTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                MainApplication(windowSizeClass)
            }
        }
    }
}

@Composable
fun MainApplication(
    windowSize: WindowSizeClass
) {
    val context = LocalContext.current
    var currentTab by rememberSaveable { mutableIntStateOf(TAB_DATA_MANAGER) }
    val onSwitchToExternalIntentManager = { currentTab = TAB_EXTERNAL_INTENT_MANAGER }
    val onSwitchToDataManager = { currentTab = TAB_DATA_MANAGER }
    val onSwitchToModelManager = { currentTab = TAB_MODEL_MANAGER }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            MainApplicationPortrait(
                currentTab = currentTab,
                onSwitchToModelManager = onSwitchToModelManager,
                onSwitchToDataManager = onSwitchToDataManager,
                onSwitchToExternalIntentManager = onSwitchToExternalIntentManager
            )
        }
        WindowWidthSizeClass.Expanded -> {
            MainApplicationLandscape(
                currentTab = currentTab,
                onSwitchToModelManager = onSwitchToModelManager,
                onSwitchToDataManager = onSwitchToDataManager,
                onSwitchToExternalIntentManager = onSwitchToExternalIntentManager
            )
        }
        else -> {
            MainApplicationPortrait(
                currentTab = currentTab,
                onSwitchToModelManager = onSwitchToModelManager,
                onSwitchToDataManager = onSwitchToDataManager,
                onSwitchToExternalIntentManager = onSwitchToExternalIntentManager
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    modifier: Modifier = Modifier,
    currentTab: Int,
    onSwitchToModelManager: () -> Unit,
    onSwitchToDataManager: () -> Unit,
    onSwitchToExternalIntentManager: () -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_navigation_apps)
                )
            },
            selected = currentTab == TAB_EXTERNAL_INTENT_MANAGER,
            onClick = { onSwitchToExternalIntentManager.invoke() }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Dataset,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_navigation_datasets)
                )
            },
            selected = currentTab == TAB_DATA_MANAGER,
            onClick = { onSwitchToDataManager.invoke() }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.ModelTraining,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_navigation_models)
                )
            },
            selected = currentTab == TAB_MODEL_MANAGER,
            onClick = { onSwitchToModelManager.invoke() }
        )
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    currentTab: Int
) {
    // Wechseln zwischen den gewählten Tabs
    when (currentTab) {
        0 -> ExternalIntentManagerComposable()
        1 -> DataManagerComposable()
        2 -> AIModelManagerComposable()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApplicationPortrait(
    currentTab: Int,
    onSwitchToModelManager: () -> Unit,
    onSwitchToDataManager: () -> Unit,
    onSwitchToExternalIntentManager: () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigation(
            modifier = Modifier,
            onSwitchToDataManager = onSwitchToDataManager,
            onSwitchToModelManager = onSwitchToModelManager,
            onSwitchToExternalIntentManager = onSwitchToExternalIntentManager,
            currentTab = currentTab
        ) }
    ) { paddingValues ->
        MainScreen(modifier = Modifier.padding(paddingValues),currentTab)
    }
}

@Composable
fun RailNavigation(
    modifier: Modifier = Modifier,
    currentTab: Int,
    onSwitchToModelManager: () -> Unit,
    onSwitchToDataManager: () -> Unit,
    onSwitchToExternalIntentManager: () -> Unit
) {
    NavigationRail(
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.bottom_navigation_apps_landscape))
                },
                selected = currentTab == TAB_EXTERNAL_INTENT_MANAGER,
                onClick = { onSwitchToExternalIntentManager.invoke() }
            )
            Spacer(modifier = Modifier.height(12.dp))
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Dataset,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.bottom_navigation_datasets))
                },
                selected = currentTab == TAB_DATA_MANAGER,
                onClick = { onSwitchToDataManager.invoke() }
            )
            Spacer(modifier = Modifier.height(12.dp))
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.ModelTraining,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.bottom_navigation_models))
                },
                selected = currentTab == TAB_MODEL_MANAGER,
                onClick = { onSwitchToModelManager.invoke() }
            )
        }
    }
}


@Composable
fun MainApplicationLandscape(
    currentTab: Int,
    onSwitchToModelManager: () -> Unit,
    onSwitchToDataManager: () -> Unit,
    onSwitchToExternalIntentManager: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row {
            RailNavigation(
                modifier = Modifier,
                currentTab = currentTab,
                onSwitchToDataManager = onSwitchToDataManager,
                onSwitchToModelManager = onSwitchToModelManager,
                onSwitchToExternalIntentManager = onSwitchToExternalIntentManager
            )
            MainScreen(modifier = Modifier, currentTab)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun BottomNavigationPreview() {
    AiXDroidTheme { BottomNavigation(Modifier.padding(top = 24.dp), TAB_DATA_MANAGER, {}, {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE, heightDp = 320)
@Composable
fun NavigationRailPreview() {
    AiXDroidTheme { RailNavigation(Modifier, TAB_DATA_MANAGER, {}, {}, {}) }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun PortraitPreview() {
    AiXDroidTheme {
        MainApplicationPortrait(TAB_DATA_MANAGER, {}, {}, {})
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun LandscapePreview() {
    AiXDroidTheme {
        MainApplicationLandscape(TAB_DATA_MANAGER, {}, {}, {})
    }
}