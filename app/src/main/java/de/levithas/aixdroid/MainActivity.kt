package de.levithas.aixdroid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.ui.datamanager.DataManagerComposable
import de.levithas.aixdroid.presentation.ui.modelmanager.AIModelManagerComposable


const val TAB_DATA_MANAGER = 0
const val TAB_MODEL_MANAGER = 1

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
    var currentTab by remember { mutableIntStateOf(TAB_DATA_MANAGER) }
    val onSwitchToDataManager = { currentTab = TAB_DATA_MANAGER }
    val onSwitchToModelManager = { currentTab = TAB_MODEL_MANAGER }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            MainApplicationPortrait(
                currentTab,
                onSwitchToModelManager,
                onSwitchToDataManager
            )
        }
        WindowWidthSizeClass.Expanded -> {
            MainApplicationLandscape(
                currentTab,
                onSwitchToModelManager,
                onSwitchToDataManager
            )
        }
        else -> {
            MainApplicationPortrait(
                currentTab,
                onSwitchToModelManager,
                onSwitchToDataManager
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    modifier: Modifier = Modifier,
    currentTab: Int,
    onSwitchToModelManager: () -> Unit,
    onSwitchToDataManager: () -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
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
        0 -> DataManagerComposable(modifier)
        1 -> AIModelManagerComposable(modifier)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApplicationPortrait(
    currentTab: Int,
    onSwitchToModelManager: () -> Unit,
    onSwitchToDataManager: () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigation(
            modifier = Modifier,
            onSwitchToDataManager = onSwitchToDataManager,
            onSwitchToModelManager = onSwitchToModelManager,
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
    onSwitchToDataManager: () -> Unit
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
    onSwitchToDataManager: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row {
            RailNavigation(
                Modifier,
                currentTab,
                onSwitchToDataManager,
                onSwitchToModelManager
            )
            MainScreen(modifier = Modifier, currentTab)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun BottomNavigationPreview() {
    AiXDroidTheme { BottomNavigation(Modifier.padding(top = 24.dp), 0, {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE, heightDp = 320)
@Composable
fun NavigationRailPreview() {
    AiXDroidTheme { RailNavigation(Modifier, TAB_DATA_MANAGER, {}, {}) }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun PortraitPreview() {
    AiXDroidTheme {
        MainApplicationPortrait(TAB_DATA_MANAGER, {}, {})
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun LandscapePreview() {
    AiXDroidTheme {
        MainApplicationLandscape(TAB_DATA_MANAGER, {}, {})
    }
}