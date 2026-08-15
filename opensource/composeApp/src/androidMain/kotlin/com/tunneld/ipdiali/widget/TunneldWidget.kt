package com.tunneld.ipdiali.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class TunneldWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataManager.read(context)

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(GlanceTheme.colors.surface)
                        .padding(12.dp)
                        .cornerRadius(12.dp),
                ) {
                    // Header row: title + refresh
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Tunnel'd",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GlanceTheme.colors.primary,
                            ),
                            modifier = GlanceModifier.defaultWeight(),
                        )
                        Text(
                            text = "\u21BB",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = GlanceTheme.colors.primary,
                            ),
                            modifier = GlanceModifier.clickable(
                                androidx.glance.appwidget.action.actionStartActivity(
                                    Intent(context, com.tunneld.ipdiali.MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    }
                                )
                            ),
                        )
                    }

                    // IP address
                    if (data.ip.isNotBlank()) {
                        Text(
                            text = data.ip,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = GlanceTheme.colors.onSurface,
                            ),
                            modifier = GlanceModifier.padding(top = 8.dp),
                        )

                        // Location line
                        val locationParts = listOfNotNull(
                            data.city,
                            data.country,
                            data.countryCode?.let { cc -> countryCodeToFlag(cc) }
                        ).ifEmpty { listOfNotNull(data.countryCode) }
                        val location = locationParts.joinToString(", ")
                        if (location.isNotBlank()) {
                            Text(
                                text = location,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                ),
                                modifier = GlanceModifier.padding(top = 2.dp),
                            )
                        }

                        // ISP/org
                        data.org?.let { org ->
                            Text(
                                text = org,
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                ),
                                modifier = GlanceModifier.padding(top = 2.dp),
                            )
                        }
                    } else {
                        Text(
                            text = "No data yet",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = GlanceTheme.colors.onSurfaceVariant,
                            ),
                            modifier = GlanceModifier.padding(top = 8.dp),
                        )
                    }

                    // Last updated
                    if (data.lastUpdated > 0) {
                        val ago = formatTimeAgo(data.lastUpdated)
                        Text(
                            text = "Updated $ago",
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = GlanceTheme.colors.onSurfaceVariant,
                            ),
                            modifier = GlanceModifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }

    private fun countryCodeToFlag(code: String): String {
        if (code.length != 2) return ""
        val base = 0x1F1E6
        val first = code[0].code - 'A'.code + base
        val second = code[1].code - 'A'.code + base
        return String(Character.toChars(first)) + String(Character.toChars(second))
    }

    private fun formatTimeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / 60_000
        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            else -> "${minutes / 60}h ago"
        }
    }
}

class TunneldWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TunneldWidget()
}