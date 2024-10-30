package de.levithas.aixdroid.presentation.ui.datamanager.dialog

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
import androidx.fragment.app.DialogFragment
import de.levithas.aixdroid.R
import de.levithas.aixdroid.presentation.ui.datamanager.DataManagerScreen

/**
 * A simple [Fragment] subclass.
 * Use the [DataExportDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DataExportDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DataExportDialog()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DataExportDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            DataExportDialogFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataExportDialog() {
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