package com.tunneld.ipdiali.application.infrastructure

import com.tunneld.ipdiali.shared.core.application.infrastructure.config.AppConfig

interface OpensourceAppConfig : AppConfig {
    val featureRequestUrl: String
    val bugReportUrl: String
}
