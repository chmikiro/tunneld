package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

interface IpApiConfig {
    val apiKey: String
}

internal object IpApiConfigImpl : IpApiConfig {
    override val apiKey: String = "D4319AEEE4C58CBD706746843FBC43FE"
}
