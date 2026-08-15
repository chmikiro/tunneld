package com.tunneld.ipdiali.shared.feature.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tunneld.ipdiali.shared.core.feature.ui.FindMyIpTheme
import com.tunneld.ipdiali.shared.core.feature.ui.LocalClipboardManager
import com.tunneld.ipdiali.shared.core.feature.ui.LocalDateFormatter
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import com.tunneld.ipdiali.shared.feature.home.persentation.AddressUiModel
import com.tunneld.ipdiali.shared.feature.home.persentation.InternetProtocolVersion
import com.tunneld.ipdiali.shared.feature.home.persentation.NetworkType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.now
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AddressButton(
    model: AddressUiModel,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    shapes: ButtonShapes = AddressButtonDefaults.singleShapes(),
    showFullEnrichment: Boolean = false,
    showNetworkType: Boolean = true,
    compact: Boolean = false,
) {
    val dateFormatter = LocalDateFormatter.current

    val timeTransition = updateTransition(model.dateTime)

    val contentPadding = if (compact)
        PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    else
        PaddingValues(horizontal = 24.dp, vertical = 14.dp)

    val ipTextStyle = if (compact)
        MaterialTheme.typography.headlineSmall
    else
        MaterialTheme.typography.headlineMediumEmphasized

    Button(
        onClick = onClick,
        modifier = modifier,
        shapes = shapes,
        contentPadding = contentPadding,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                timeTransition.AnimatedContent(
                    contentKey = { it.toString() },
                    transitionSpec = {
                        fadeIn(tween(durationMillis = 1_000)) togetherWith
                            fadeOut(tween(durationMillis = 200))
                    },
                ) {
                    Text(
                        text = dateFormatter.formatDateTimeLong(model.dateTime),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(Modifier.weight(1f))
                if (showNetworkType) {
                    model.networkType.Icon()
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = model.networkType.stringResource(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Text(
                text = model.address,
                style = ipTextStyle,
                fontWeight = FontWeight.Bold,
            )
            model.ipInfo?.let { info ->
                Spacer(Modifier.height(4.dp))
                if (showFullEnrichment) {
                    // Full enrichment: flag + city, CC + · + ISP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val flag = info.countryCode?.let { countryCodeToFlag(it) } ?: ""
                        if (flag.isNotEmpty()) {
                            Text(
                                text = flag,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        val location =
                            listOfNotNull(info.city, info.countryCode).joinToString(", ")
                        if (location.isNotEmpty()) {
                            Text(
                                text = location,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        info.isp?.let { ispName ->
                            Text(
                                text = "·",
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.4f),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = ispName,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                        }
                    }
                } else {
                    // History: country-only
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        info.countryCode?.let { code ->
                            Text(
                                text = countryCodeToFlag(code),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        info.country?.let { country ->
                            Text(
                                text = country,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }
            model.domain?.let { domain ->
                Text(
                    text = domain,
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}

object AddressButtonDefaults {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    private val pressedShape: Shape
        @Composable get() = MaterialTheme.shapes.extraExtraLarge

    private val middleCornerRadius: CornerSize
        @Composable get() = MaterialTheme.shapes.medium.bottomEnd

    private val outerCornerRadius: CornerSize
        @Composable get() = MaterialTheme.shapes.extraLarge.topStart

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun topShapes(): ButtonShapes =
        ButtonDefaults.shapes(
            shape =
                RoundedCornerShape(
                    outerCornerRadius,
                    outerCornerRadius,
                    middleCornerRadius,
                    middleCornerRadius,
                ),
            pressedShape = pressedShape,
        )

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun middleShapes(): ButtonShapes =
        ButtonDefaults.shapes(
            shape = RoundedCornerShape(middleCornerRadius),
            pressedShape = pressedShape,
        )

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun bottomShapes(): ButtonShapes =
        ButtonDefaults.shapes(
            shape =
                RoundedCornerShape(
                    middleCornerRadius,
                    middleCornerRadius,
                    outerCornerRadius,
                    outerCornerRadius,
                ),
            pressedShape = pressedShape,
        )

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun singleShapes(): ButtonShapes =
        ButtonDefaults.shapes(
            shape = RoundedCornerShape(outerCornerRadius),
            pressedShape = pressedShape,
        )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AddressButtonPreview() {
    val model =
        object : AddressUiModel {
            override val internetProtocolVersion: InternetProtocolVersion =
                InternetProtocolVersion.IPV4
            override val address: String = "8.8.8.8"
            override val domain: String? = "google.com"
            override val dateTime: LocalDateTime = LocalDateTime.now()
            override val networkType: NetworkType = NetworkType.WIFI
            override val ipInfo: IpInfo? = null
        }

    FindMyIpTheme {
        AddressButton(
            model = model,
            onClick = {},
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
internal fun IpDetailDialog(
    model: AddressUiModel,
    onDismiss: () -> Unit,
    onCopyIp: (String) -> Unit,
) {
    val dateFormatter = LocalDateFormatter.current
    var showFieldSelector by remember { mutableStateOf(false) }
    val selectedFields = remember { mutableStateMapOf<String, Boolean>() }
    val networkTypeStr = model.networkType.stringResource()
    val ipLabel =
        when (model.internetProtocolVersion) {
            InternetProtocolVersion.IPV4 -> "IPv4 address"
            InternetProtocolVersion.IPV6 -> "IPv6 address"
        }

    // Build available fields map
    val fields = remember(model) {
        val map = linkedMapOf<String, String>()
        map["Address"] = model.address
        map["Version"] = model.internetProtocolVersion.name
        map["Network"] = networkTypeStr
        model.ipInfo?.let { info ->
            info.country?.let { map["Country"] = "$it ${countryCodeToFlag(info.countryCode ?: "")}" }
            info.countryCode?.let { map["Country Code"] = it }
            info.city?.let { map["City"] = it }
            info.isp?.let { map["ISP"] = it }
            info.org?.let { map["Organization"] = it }
            info.asn?.let { map["ASN"] = "AS$it" }
            info.timezone?.let { map["Timezone"] = it }
            if (info.latitude != null && info.longitude != null) {
                map["Coordinates"] = "%.4f, %.4f".format(info.latitude, info.longitude)
            }
        }
        model.domain?.let { map["Domain"] = it }
        map["Timestamp"] = dateFormatter.formatDateTimeLong(model.dateTime)
        map
    }

    fun buildCopyText(): String {
        val selected = fields.filterKeys { selectedFields[it] == true }
        return if (selected.isEmpty()) model.address
        else selected.entries.joinToString("; ") { "${it.key}: ${it.value}" }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp),
                ) {
                    Text(
                        text = ipLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = model.address,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    model.domain?.let { domain ->
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = domain,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Detail rows
                model.ipInfo?.let { info ->
                    DividerHeader("Location")
                    IpDetailRow("Country", listOfNotNull(info.country, info.countryCode?.let { countryCodeToFlag(it) }).joinToString(" "))
                    info.city?.let { IpDetailRow("City", it) }
                    info.region?.let { IpDetailRow("Region", it) }
                    info.timezone?.let { IpDetailRow("Timezone", it) }
                    info.latitude?.let { lat ->
                        info.longitude?.let { lng ->
                            IpDetailRow("Coordinates", String.format("%.4f, %.4f", lat, lng))
                        }
                    }

                    DividerHeader("Network")
                    info.isp?.let { IpDetailRow("ISP", it) }
                    info.org?.let { IpDetailRow("Organization", it) }
                    info.asn?.let { IpDetailRow("ASN", "AS$it") }
                }

                // Metadata
                DividerHeader("Connection")
                IpDetailRow("Network type", model.networkType.stringResource())
                IpDetailRow("Last updated", dateFormatter.formatDateTimeLong(model.dateTime))

                Spacer(Modifier.height(8.dp))

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    Button(
                        onClick = { onCopyIp(buildCopyText()); onDismiss() },
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(if (selectedFields.isEmpty()) "Copy IP" else "Copy Selected")
                    }
                    TextButton(onClick = { showFieldSelector = true }) {
                        Text("Select Fields")
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }

    if (showFieldSelector) {
        FieldSelectorDialog(
            fields = fields,
            selectedFields = selectedFields,
            onDismiss = { showFieldSelector = false },
        )
    }
}

@Composable
private fun FieldSelectorDialog(
    fields: Map<String, String>,
    selectedFields: MutableMap<String, Boolean>,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "Select fields to copy",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(24.dp, 24.dp, 24.dp, 8.dp),
                )
                Column(modifier = Modifier.selectableGroup()) {
                    fields.forEach { (label, value) ->
                        val isSelected = selectedFields[label] == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = { selectedFields[label] = !isSelected },
                                    role = Role.Checkbox,
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { selectedFields[label] = it },
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(onClick = { selectedFields.clear(); onDismiss() }) {
                        Text("Clear")
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun DividerHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 14.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            letterSpacing = 1.5.sp,
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(3f)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        )
    }
}

@Composable
private fun IpDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.weight(0.35f),
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.65f),
        )
    }
}

/**
 * Converts a 2-letter country code to its flag emoji.
 * e.g. "MA" → "🇲🇦", "US" → "🇺🇸"
 * Uses pure Kotlin surrogate-pair math — works on all platforms.
 */
internal fun countryCodeToFlag(code: String): String {
    if (code.length != 2) return ""
    return code.uppercase().map { char ->
        val codePoint = 0x1F1E6 + (char.code - 'A'.code)
        val high = (0xD800 + ((codePoint - 0x10000) shr 10)).toChar()
        val low = (0xDC00 + ((codePoint - 0x10000) and 0x3FF)).toChar()
        "$high$low"
    }.joinToString("")
}
