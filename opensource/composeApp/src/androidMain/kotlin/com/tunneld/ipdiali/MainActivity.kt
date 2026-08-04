package com.tunneld.ipdiali

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tunneld.ipdiali.feature.dashboard.DashboardRoute
import com.tunneld.ipdiali.shared.core.feature.ui.ProvideUtilities
import com.tunneld.ipdiali.shared.core.presentation.AndroidClipboardManager
import com.tunneld.ipdiali.shared.core.presentation.AndroidDateFormatter
import com.tunneld.ipdiali.ui.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class MainActivity : AppCompatActivity() {
    private var pendingCsv: String? = null
    private val createDocument =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
            uri?.let { writeCsvToUri(it) }
        }
    private val openDocument =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { importCsvFromUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ProvideUtilities(
                dateFormatter = AndroidDateFormatter(this),
                clipboardManager = AndroidClipboardManager(this),
                content = {
                    App(
                        onExportCsv = { csvContent ->
                            pendingCsv = csvContent
                            createDocument.launch("tunneld-export.csv")
                        },
                        onImportCsv = {
                            openDocument.launch(arrayOf("text/*", "*/*"))
                        },
                        dashboardComposable = {
                            DashboardRoute(onBack = { finish() })
                        },
                    )
                },
            )
        }
    }

    private fun writeCsvToUri(uri: Uri) {
        val csv = pendingCsv ?: return
        pendingCsv = null
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(csv.toByteArray(Charsets.UTF_8))
        }
    }

    private fun importCsvFromUri(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val csvContent = contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: return@launch
                val koin = GlobalContext.get()
                val useCase = koin.get<com.tunneld.ipdiali.shared.core.application.usecase.ImportCsvUseCase>()
                useCase.import(csvContent)
            } catch (_: Exception) { }
        }
    }
}
