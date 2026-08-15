package com.tunneld.ipdiali.shared.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import com.tunneld.ipdiali.shared.feature.home.persentation.CurrentAddressUiModel
import com.tunneld.ipdiali.shared.feature.home.persentation.InternetProtocolVersion
import com.tunneld.ipdiali.shared.feature.home.persentation.NetworkType
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class, kotlin.time.ExperimentalTime::class)
@Composable
internal fun LookupExternalIpModal(
    onDismiss: () -> Unit,
    lookupIp: suspend (String) -> IpInfo?,
    onCopyIp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    var ipInput by remember { mutableStateOf("") }
    var resultModel by remember { mutableStateOf<CurrentAddressUiModel.Address?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var detailModel by remember { mutableStateOf<CurrentAddressUiModel.Address?>(null) }

    LaunchedEffect(ipInput) {
        if (ipInput.isBlank()) {
            resultModel = null
            return@LaunchedEffect
        }
        delay(500)
        isSearching = true
        val info = lookupIp(ipInput)
        isSearching = false
        resultModel = info?.let {
            CurrentAddressUiModel.Address(
                address = ipInput,
                domain = null,
                dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                internetProtocolVersion = if (ipInput.contains(':')) InternetProtocolVersion.IPV6 else InternetProtocolVersion.IPV4,
                networkType = NetworkType.UNKNOWN,
                ipInfo = it,
            )
        }
    }

    detailModel?.let { model ->
        IpDetailDialog(
            model = model,
            onDismiss = { detailModel = null },
            onCopyIp = onCopyIp,
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Lookup External IP",
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = ipInput,
                onValueChange = { ipInput = it },
                label = { Text("IP address") },
                placeholder = { Text("e.g. 8.8.8.8") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            if (isSearching) {
                Text(
                    "Looking up...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            resultModel?.let { model ->
                AddressButton(
                    model = model,
                    onClick = { detailModel = model },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    showNetworkType = false,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
