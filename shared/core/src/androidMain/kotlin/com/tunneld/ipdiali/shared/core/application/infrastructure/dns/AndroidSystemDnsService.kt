package com.tunneld.ipdiali.shared.core.application.infrastructure.dns

import com.tunneld.ipdiali.shared.core.domain.IpAddress
import java.net.InetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AndroidSystemDnsService : DnsService {
    override suspend fun reverseLookup(ip: IpAddress): String? {
        val string = ip.stringRepresentation()
        val addr = InetAddress.getByName(string)
        val hostName: String? = withContext(Dispatchers.IO) { addr.hostName }
        // When no PTR record exists, hostName equals the IP itself — suppress to avoid duplication
        return if (hostName == string) null else hostName
    }
}
