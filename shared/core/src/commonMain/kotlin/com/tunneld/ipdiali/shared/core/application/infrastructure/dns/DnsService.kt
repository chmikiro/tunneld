package com.tunneld.ipdiali.shared.core.application.infrastructure.dns

import com.tunneld.ipdiali.shared.core.domain.IpAddress

interface DnsService {
    suspend fun reverseLookup(ip: IpAddress): String?
}
