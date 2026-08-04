package com.tunneld.ipdiali.shared.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.tunneld.ipdiali.shared.core.feature.ui.LocalClipboardManager
import com.tunneld.ipdiali.shared.feature.home.persentation.AddressUiModel
import com.tunneld.ipdiali.shared.feature.home.persentation.AddressHistoryUiModel
import com.tunneld.ipdiali.shared.feature.home.persentation.CurrentAddressUiModel
import com.tunneld.ipdiali.shared.feature.home.persentation.Filter
import com.tunneld.ipdiali.shared.feature.home.persentation.isAvailable
import tunneld.composeapp.generated.resources.Res
import tunneld.composeapp.generated.resources.headline_current
import tunneld.composeapp.generated.resources.headline_history
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class,
)
@Composable
internal fun HomeScreen(
    ip4: CurrentAddressUiModel,
    ip6: CurrentAddressUiModel,
    history: LazyPagingItems<AddressHistoryUiModel>,
    filter: Filter,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSearch: (String) -> Unit,
    onSettings: () -> Unit,
    onFilterUpdate: (Filter) -> Unit,
    onExportCsv: () -> Unit,
    onDashboard: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val pullState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showFilters by rememberSaveable { mutableStateOf(false) }
    var detailModel by remember { mutableStateOf<AddressUiModel?>(null) }

    if (showFilters) {
        FiltersModal(
            filter = filter,
            onDismiss = { showFilters = false },
            onUpdateFilter = onFilterUpdate,
        )
    }

    detailModel?.let { model ->
        IpDetailDialog(
            model = model,
            onDismiss = { detailModel = null },
            onCopyIp = {
                clipboardManager.copyToClipboard(it)
                scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
            },
        )
    }

    val searchTextState = rememberTextFieldState()
    LaunchedEffect(searchTextState, onSearch) {
        snapshotFlow { searchTextState.text }
            .debounce(100)
            .collectLatest { onSearch(it.toString()) }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeTopBar(
                filter = filter,
                searchTextState = searchTextState,
                onSearch = onSearch,
                onSettings = onSettings,
                onExportCsv = onExportCsv,
                onDashboard = onDashboard,
                onFilter = { showFilters = true },
            )
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullState,
            indicator = {
                MyCustomIndicator(
                    state = pullState,
                    isRefreshing = isRefreshing,
                    contentPadding = PaddingValues(top = paddingValues.calculateTopPadding()),
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            },
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(360.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues.add(horizontal = 16.dp),
            ) {
                if (ip4 is CurrentAddressUiModel.Address || ip6 is CurrentAddressUiModel.Address) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = stringResource(Res.string.headline_current),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                }

                if (ip4 is CurrentAddressUiModel.Address) {
                    item(key = "ip4") {
                        AddressButton(
                            model = ip4,
                            onClick = { detailModel = ip4 },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 2.dp),
                            shapes =
                                if (ip6.isAvailable()) AddressButtonDefaults.topShapes()
                                else AddressButtonDefaults.singleShapes(),
                            showFullEnrichment = true,
                        )
                    }
                }

                if (ip6 is CurrentAddressUiModel.Address) {
                    item(key = "ip6") {
                        AddressButton(
                            model = ip6,
                            onClick = { detailModel = ip6 },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shapes =
                                if (ip4.isAvailable()) AddressButtonDefaults.bottomShapes()
                                else AddressButtonDefaults.singleShapes(),
                            showFullEnrichment = true,
                        )
                    }
                }

                if (history.itemCount > 0) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        val applyTopPadding = ip4.isAvailable() || ip6.isAvailable()
                        Text(
                            text = stringResource(Res.string.headline_history),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier =
                                Modifier.padding(
                                    top = if (applyTopPadding) 8.dp else 0.dp,
                                    bottom = 4.dp,
                                ),
                        )
                    }
                }

                items(count = history.itemCount, key = history.itemKey { it.id }) { index ->
                    val item = history[index] ?: return@items

                    val shapes =
                        when (index) {
                            0 -> AddressButtonDefaults.topShapes()
                            history.itemCount - 1 -> AddressButtonDefaults.bottomShapes()
                            else -> AddressButtonDefaults.middleShapes()
                        }

                    AddressButton(
                        model = item,
                        onClick = {
                            clipboardManager.copyToClipboard(item.address)
                            scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.animateItem().padding(bottom = 2.dp),
                        shapes = shapes,
                        compact = true,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MyCustomIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val topPadding = contentPadding.calculateTopPadding()

    Box(
        modifier =
            modifier.graphicsLayer {
                alpha = if (state.distanceFraction == 0f) 0f else 1f
                translationY =
                    topPadding.roundToPx() - size.height +
                        state.distanceFraction * size.height +
                        8.dp.roundToPx()
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isRefreshing) {
            ContainedLoadingIndicator(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                indicatorColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        } else {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier =
                            Modifier.size(32.dp).graphicsLayer {
                                this.rotationZ = state.distanceFraction * 90f
                            },
                    )
                }
            }
        }
    }
}
