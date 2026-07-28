package com.tunneld.ipdiali.infrastructure.room

import androidx.room.RoomDatabaseConstructor

/**
 * Room KSP 2.7+ multiplatform constructor.
 * The actual database construction uses Builder.buildDatabase() in AppModule.
 */
@Suppress("unused")
internal expect object FindMyIpDatabaseCtor : RoomDatabaseConstructor<FindMyIpDatabase>
