package com.tunneld.ipdiali.shared.core.domain.event

import com.tunneld.ipdiali.shared.core.domain.AddressHistory

data class IpAddressChangedEvent(val newAddress: AddressHistory) : DomainEvent
