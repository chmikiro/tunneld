package com.tunneld.ipdiali.shared.feature.dashboard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tunneld.ipdiali.shared.core.application.usecase.ExportAddressHistoryUseCase
import com.tunneld.ipdiali.shared.core.domain.AddressHistory
import com.tunneld.ipdiali.shared.core.domain.NetworkType
import com.tunneld.ipdiali.shared.core.feature.ui.ArrowBackIconButton
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

// ── Data types ──

data class ChartItem(
    val label: String,
    val value: Float,
    val color: Color,
)

private enum class TimeRange(val label: String) {
    All("All time"),
    Last30("Last 30 days"),
    Last7("Last 7 days"),
    Today("Today"),
}

// ── Color palette ──

private val chartColors = listOf(
    Color(0xFF4A6790), Color(0xFF6B8FB5), Color(0xFF8DB4D8),
    Color(0xFF5A7DA8), Color(0xFF3D5A80), Color(0xFF7BA0C0),
    Color(0xFF9DC8E0), Color(0xFFB8D8F0), Color(0xFF2C4A70),
    Color(0xFF8EACD0),
)

private val networkColors = mapOf(
    "VPN" to Color(0xFF4A6790),
    "WiFi" to Color(0xFF6B8FB5),
    "Cellular" to Color(0xFFA8D0E6),
    "Unknown" to Color(0xFFCCCCCC),
)

// ── Screen ──

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val exportUseCase: ExportAddressHistoryUseCase = koinInject()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var countryData by remember { mutableStateOf<List<ChartItem>>(emptyList()) }
    var networkTypeData by remember { mutableStateOf<List<ChartItem>>(emptyList()) }
    var totalConnections by remember { mutableStateOf(0) }
    var totalCountries by remember { mutableStateOf(0) }
    var selectedRange by remember { mutableStateOf(TimeRange.All) }

    LaunchedEffect(selectedRange) {
        val allHistory = exportUseCase.export(
            query = null, ipv4 = true, ipv6 = true,
            networkTypes = setOf(NetworkType.WiFi, NetworkType.Cellular, NetworkType.VPN, NetworkType.Unknown),
        )
        val history = filterByRange(allHistory, selectedRange)
        totalConnections = history.size
        totalCountries = countAllCountries(history)
        countryData = aggregateByCountry(history)
        networkTypeData = aggregateByNetworkType(history)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = { ArrowBackIconButton(onClick = onBack) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            // Time range filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TimeRange.entries.forEach { range ->
                    FilterChip(
                        selected = selectedRange == range,
                        onClick = { selectedRange = range },
                        label = { Text(range.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatItem("Connections", totalConnections.toString())
                    StatItem("Countries", totalCountries.toString())
                    StatItem(
                        "VPN",
                        "${networkTypeData.find { it.label == "VPN" }?.value?.toInt() ?: 0}",
                    )
                }
            }

            // Country bar chart
            if (countryData.isNotEmpty()) {
                Text(
                    "Connections by Country",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                BarChart(
                    items = countryData,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                )
            } else {
                Text(
                    "No data for this period",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Network type section
            if (networkTypeData.isNotEmpty()) {
                Text(
                    "Network Type Split",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DonutChart(
                        items = networkTypeData,
                        modifier = Modifier.size(160.dp),
                    )
                    Column(modifier = Modifier.padding(start = 24.dp)) {
                        networkTypeData.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp),
                            ) {
                                Canvas(Modifier.size(12.dp)) {
                                    drawCircle(color = item.color, radius = 6.dp.toPx())
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "${item.label}: ${item.value.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
        )
    }
}

// ── Bar Chart ──

@Composable
fun BarChart(
    items: List<ChartItem>,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val maxVal = items.maxOfOrNull { it.value } ?: 1f
    val labelStyle = TextStyle(fontSize = 10.sp, color = Color.Gray)
    val valueStyle = TextStyle(fontSize = 9.sp, color = Color.DarkGray)

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height
        val barCount = items.size
        val totalGap = (barCount + 1) * chartWidth * 0.04f
        val barWidth = (chartWidth - totalGap) / barCount
        val labelAreaHeight = 24.dp.toPx()

        items.forEachIndexed { i, item ->
            val barHeight = (item.value / maxVal) * (chartHeight - labelAreaHeight) * 0.88f
            val x = chartWidth * 0.04f + i * (barWidth + chartWidth * 0.04f)
            val y = chartHeight - labelAreaHeight - barHeight

            drawRect(
                color = item.color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
            )

            // Label (truncated to 5 chars)
            val shortLabel = if (item.label.length > 5) item.label.take(5) + "\u2026" else item.label
            val labelResult = textMeasurer.measure(
                text = shortLabel,
                style = labelStyle,
                maxLines = 1,
            )
            drawText(
                textLayoutResult = labelResult,
                topLeft = Offset(
                    x + barWidth / 2 - labelResult.size.width / 2,
                    chartHeight - labelAreaHeight + 4.dp.toPx(),
                ),
            )

            // Value above bar (if tall enough)
            if (barHeight > 36.dp.toPx()) {
                val valText = item.value.toInt().toString()
                val valResult = textMeasurer.measure(valText, style = valueStyle, maxLines = 1)
                drawText(
                    textLayoutResult = valResult,
                    topLeft = Offset(
                        x + barWidth / 2 - valResult.size.width / 2,
                        y - valResult.size.height - 2.dp.toPx(),
                    ),
                )
            }
        }
    }
}

// ── Donut Chart ──

@Composable
fun DonutChart(
    items: List<ChartItem>,
    modifier: Modifier = Modifier,
) {
    val total = items.sumOf { it.value.toDouble() }.toFloat()
    val textMeasurer = rememberTextMeasurer()
    val centerStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)

    if (total == 0f) return

    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.22f
        val radius = (size.minDimension - strokeWidth) / 2f
        val center = Offset(size.width / 2, size.height / 2)
        var startAngle = -90f

        items.forEach { item ->
            val sweepAngle = (item.value / total) * 360f
            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth),
            )
            startAngle += sweepAngle
        }

        // Center total
        val centerText = textMeasurer.measure(total.toInt().toString(), style = centerStyle)
        drawText(
            textLayoutResult = centerText,
            topLeft = Offset(
                center.x - centerText.size.width / 2,
                center.y - centerText.size.height / 2,
            ),
        )
    }
}

// ── Aggregation ──

@OptIn(ExperimentalTime::class)
private fun filterByRange(history: List<AddressHistory>, range: TimeRange): List<AddressHistory> {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    if (range == TimeRange.All) return history

    // Compute cutoff using LocalDate comparison
    val todayEpoch = today.toEpochDays()
    val cutoffEpoch = when (range) {
        TimeRange.Today -> todayEpoch
        TimeRange.Last7 -> todayEpoch - 6
        TimeRange.Last30 -> todayEpoch - 29
        TimeRange.All -> todayEpoch
    }

    return when (range) {
        TimeRange.Today -> history.filter { it.dateTime.date == today }
        TimeRange.Last7, TimeRange.Last30 -> history.filter {
            it.dateTime.date.toEpochDays() >= cutoffEpoch
        }
        TimeRange.All -> history
    }
}

private fun countAllCountries(history: List<AddressHistory>): Int {
    return history
        .mapNotNull { item ->
            val info = when (item) {
                is AddressHistory.Ipv4 -> item.ipInfo
                is AddressHistory.Ipv6 -> item.ipInfo
            }
            info?.country
        }
        .toSet()
        .size
}

private fun aggregateByCountry(history: List<AddressHistory>): List<ChartItem> {
    return history
        .mapNotNull { item ->
            val info = when (item) {
                is AddressHistory.Ipv4 -> item.ipInfo
                is AddressHistory.Ipv6 -> item.ipInfo
            }
            info?.country ?: "Unknown"
        }
        .groupBy { it }
        .mapValues { it.value.size }
        .entries
        .sortedByDescending { it.value }
        .take(10)
        .mapIndexed { i, (label, count) ->
            ChartItem(label = label, value = count.toFloat(), color = chartColors[i % chartColors.size])
        }
}

private fun aggregateByNetworkType(history: List<AddressHistory>): List<ChartItem> {
    return history
        .groupBy { it.networkType }
        .mapValues { it.value.size }
        .entries
        .sortedByDescending { it.value }
        .map { (type, count) ->
            val label = type.toString().substringAfterLast(".")
            ChartItem(
                label = label,
                value = count.toFloat(),
                color = networkColors[label] ?: Color.Gray,
            )
        }
}