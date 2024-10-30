package de.levithas.aixdroid.presentation.ui.modelmanager

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import de.levithas.aixdroid.R
import de.levithas.aixdroid.presentation.ui.datamanager.DataManagerScreen

class AIModelManagerFragment : Fragment() {

    companion object {
        fun newInstance() = AIModelManagerFragment()
    }

    private val viewModel: AIViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AIModelManagerScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelManagerScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Manager") },
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