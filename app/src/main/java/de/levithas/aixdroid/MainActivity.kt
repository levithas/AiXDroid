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
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.levithas.aixdroid.presentation.theme.AiXDroidTheme
import de.levithas.aixdroid.presentation.ui.datamanager.DataManagerComposable


class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MainApplication(windowSizeClass)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApplication(
    windowSize: WindowSizeClass
) {
    val context = LocalContext.current

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            MainApplicationPortrait()
        }
        WindowWidthSizeClass.Expanded -> {
            MainApplicationLandscape()
        }
    }

}

@Composable
private fun BottomNavigation(modifier: Modifier = Modifier) {
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
            selected = true,
            onClick = {}
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
            selected = false,
            onClick = {}
        )
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    // Wechsel zwischen den gewählten Ansichten
    DataManagerComposable()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApplicationPortrait() {
    AiXDroidTheme {
        Scaffold(
            bottomBar = { BottomNavigation() }
        ) { paddingValues ->
            MainScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun RailNavigation(modifier: Modifier = Modifier) {
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
                selected = true,
                onClick = {}
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
                selected = false,
                onClick = {}
            )
        }
    }
}


@Composable
fun MainApplicationLandscape() {
    AiXDroidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Row {
                RailNavigation()
                MainScreen()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun BottomNavigationPreview() {
    AiXDroidTheme() { BottomNavigation(Modifier.padding(top = 24.dp)) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE, heightDp = 360)
@Composable
fun NavigationRailPreview() {
    AiXDroidTheme() { RailNavigation() }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun PortraitPreview() {
    MainApplicationPortrait()
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun LandscapePreview() {
    MainApplicationLandscape()
}