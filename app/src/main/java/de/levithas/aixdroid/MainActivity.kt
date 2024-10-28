package de.levithas.aixdroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import de.levithas.aixdroid.ml.TestModel
import de.levithas.aixdroid.ui.theme.AiXDroidTheme
import org.tensorflow.lite.schema.Metadata
import org.tensorflow.lite.support.metadata.MetadataExtractor
import kotlin.io.path.Path


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiXDroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TFMetadataPreviewWindow(modelConfiguration: ModelConfiguration, modifier: Modifier) {
    Card(
        modifier = modifier
    ) {
        Column(
        ) {
            Text(
                "Hallo Levi!"
            )
            Text(
                "Modellname: " + modelConfiguration.meta.name()
            )
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    AiXDroidTheme {
        TFMetadataPreviewWindow(ModelConfiguration("test", Path("test"), Metadata()) , modifier = Modifier)
    }
}