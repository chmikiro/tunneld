package com.tunneld.ipdiali.screenshot

import androidx.compose.runtime.Composable
import com.tunneld.ipdiali.shared.feature.home.ui.HomeScreenPreview

data object HomeScreenScreenshot : Screenshot {
    override val name: String = "1"

    @Composable override fun Content() = HomeScreenPreview()
}
