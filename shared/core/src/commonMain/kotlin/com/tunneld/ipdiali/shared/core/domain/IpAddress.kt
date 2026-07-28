package com.tunneld.ipdiali.shared.core.domain

sealed interface IpAddress : IpAddressString

interface IpAddressString {
    fun stringRepresentation(): String
}
