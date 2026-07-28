package com.tunneld.ipdiali.shared.core.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AddressHistory")
data class AddressHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val address: String,
    val domain: String?,
    val addressVersion: AddressVersion,
    val networkType: NetworkType,
    val epochSeconds: Long,
    val country: String? = null,
    val countryCode: String? = null,
    val city: String? = null,
    val isp: String? = null,
    val org: String? = null,
    val timezone: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
)
