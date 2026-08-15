package com.tunneld.ipdiali.shared.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tunneld.ipdiali.shared.feature.home.persentation.Filter
import com.tunneld.ipdiali.shared.core.feature.ui.ThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTopBar(
    filter: Filter,
    onSettings: () -> Unit,
    onDashboard: () -> Unit,
    onFilter: () -> Unit,
    onLookup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarColor = if (ThemeState.isTopBarTranslucent) Color.Transparent else MaterialTheme.colorScheme.background
    androidx.compose.material3.Surface(
        color = topBarColor,
        modifier = modifier.fillMaxWidth(),
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // App branding — bold text matching icon height
        Text(
            "Tunnel'd",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
            modifier = Modifier.height(32.dp),
        )

        Spacer(Modifier.weight(1f))

        // Lookup external IP button — centered between icon and filter
        OutlinedButton(
            onClick = onLookup,
            modifier = Modifier.height(40.dp),
        ) {
            Text(
                "Lookup external IP",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Spacer(Modifier.weight(1f))

        // Right-aligned icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Filter icon with badge
            TopBarIconButton(
                onClick = onFilter,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                if (filter.filtersCount > 0) {
                    BadgedBox(badge = { Badge { } }) {
                        Icon(Icons.Outlined.FilterList, null)
                    }
                } else {
                    Icon(Icons.Outlined.FilterList, null)
                }
            }

            // Analytics
            TopBarIconButton(
                onClick = onDashboard,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Icon(Icons.Outlined.Analytics, null)
            }

            // Settings
            TopBarIconButton(
                onClick = onSettings,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Icon(Icons.Filled.Settings, null)
            }
        }
    }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBarIconButton(
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    IconButton(
        shapes = IconButtonDefaults.shapes(),
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        content()
    }
}