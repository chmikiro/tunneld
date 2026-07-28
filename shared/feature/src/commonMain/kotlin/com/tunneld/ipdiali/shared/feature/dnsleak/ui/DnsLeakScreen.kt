package com.tunneld.ipdiali.shared.feature.dnsleak.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tunneld.ipdiali.shared.feature.dnsleak.presentation.DnsLeakResult
import com.tunneld.ipdiali.shared.feature.dnsleak.presentation.DnsLeakState
import com.tunneld.ipdiali.shared.feature.dnsleak.presentation.DnsLeakViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DnsLeakScreen(
    modifier: Modifier = Modifier,
    viewModel: DnsLeakViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header
        Text(
            text = "ipdia.li",
            fontSize = 36.sp,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-2).sp,
        )
        Text(
            text = "dns leak test",
            fontSize = 13.sp,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-0.5).sp,
        )

        Spacer(Modifier.height(32.dp))

        // Test card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (val s = state) {
                    is DnsLeakState.Idle -> {
                        Text(
                            "Check your DNS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "See which DNS servers your device is actually using — not the ones you think.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.startTest() },
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Run leak test")
                        }
                    }

                    is DnsLeakState.Running -> {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Running DNS leak test...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                        )
                    }

                    is DnsLeakState.Error -> {
                        Text(
                            s.message,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { viewModel.startTest() },
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Try again")
                        }
                    }

                    is DnsLeakState.Done -> {
                        if (s.results.isEmpty()) {
                            Text(
                                "No DNS queries detected.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.startTest() },
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("Try again")
                            }
                        } else {
                            Text(
                                "DNS servers detected",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W600,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${s.results.size} server${if (s.results.size > 1) "s" else ""} resolved",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(16.dp))
                            s.results.forEach { result ->
                                DnsResultCard(result, viewModel)
                            }
                            Spacer(Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = { viewModel.startTest() },
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("Run again")
                            }
                        }
                    }
                }
            }
        }

        // Context note (shown with results)
        if (state is DnsLeakState.Done && (state as DnsLeakState.Done).results.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "This shows which DNS servers your device actually used. If you're on a VPN or proxy and you see your ISP's IP range here, your DNS queries are bypassing the tunnel.",
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DnsResultCard(result: DnsLeakResult, viewModel: DnsLeakViewModel, modifier: Modifier = Modifier) {
    var copyFeedback by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Country flag
                Box(
                    modifier = Modifier
                        .size(28.dp, 20.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        result.country.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W800,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        result.isp,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(2.dp))
                    // IP + city
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            result.ip,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        val location = if (result.city.isNotEmpty()) {
                            "${result.city}, ${result.country.uppercase()}"
                        } else {
                            result.country.uppercase()
                        }
                        Text(
                            "  ·  $location",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Action row: ASN link + copy IP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Clickable ASN — opens external browser with confirmation
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            viewModel.openExternalBrowser(
                                "https://bgp.he.net/AS${result.asn}",
                                "AS${result.asn} Details",
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInBrowser,
                        contentDescription = "Open ASN details",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "AS${result.asn}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.W500,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Copy IP button
                IconButton(
                    onClick = {
                        viewModel.copyToClipboard(result.ip)
                        copyFeedback = true
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = if (copyFeedback) "Copied!" else "Copy IP",
                        modifier = Modifier.size(18.dp),
                        tint = if (copyFeedback)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}
