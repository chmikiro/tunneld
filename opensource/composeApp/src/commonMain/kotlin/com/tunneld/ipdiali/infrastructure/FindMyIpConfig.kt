package com.tunneld.ipdiali.infrastructure

import Tunneld.opensource.composeApp.BuildConfig
import com.tunneld.ipdiali.application.infrastructure.OpensourceAppConfig

internal class FindMyIpConfig : OpensourceAppConfig {
    override val appUrl: String = BuildConfig.GITHUB_URL
    override val featureRequestUrl: String = BuildConfig.GITHUB_ISSUES_URL
    override val bugReportUrl: String = BuildConfig.GITHUB_ISSUES_URL
    override val translateUrl: String = BuildConfig.CROWDIN_URL
    override val feedbackEmailUri: String = BuildConfig.FEEDBACK_EMAIL_URI
}
