package com.tunneld.ipdiali.infrastructure.room

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Migration from 5.x to 6.x — adds IP enrichment columns
 * (country, city, ISP, org, timezone, geolocation).
 */
internal object FindMyIP6Migration : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN country TEXT")
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN countryCode TEXT")
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN city TEXT")
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN isp TEXT")
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN org TEXT")
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN timezone TEXT")
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN latitude REAL")
        connection.execSQL("ALTER TABLE AddressHistory ADD COLUMN longitude REAL")
    }
}
