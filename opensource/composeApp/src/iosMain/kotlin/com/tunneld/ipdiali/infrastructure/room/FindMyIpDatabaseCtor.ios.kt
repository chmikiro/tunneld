package com.tunneld.ipdiali.infrastructure.room

import androidx.room.RoomDatabaseConstructor

@Suppress("unused")
internal actual object FindMyIpDatabaseCtor : RoomDatabaseConstructor<FindMyIpDatabase> {
    override fun initialize(): FindMyIpDatabase {
        error("iOS not implemented")
    }
}
