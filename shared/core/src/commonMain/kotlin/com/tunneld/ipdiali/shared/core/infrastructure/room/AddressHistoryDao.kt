package com.tunneld.ipdiali.shared.core.infrastructure.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AddressHistoryDao {

    @Query(
        """
        SELECT * 
        FROM AddressHistory
        WHERE
            (:query IS NULL OR address LIKE '%' || :query || '%') AND
            (
                (addressVersion = CASE WHEN :ipv4 THEN ${AddressVersionSQLConstants.IPV4} ELSE -1 END) OR
                (addressVersion = CASE WHEN :ipv6 THEN ${AddressVersionSQLConstants.IPV6} ELSE -1 END)
            ) AND
            (:country IS NULL OR LOWER(country) LIKE '%' || LOWER(:country) || '%') AND
            (
                (:wifi = 0 AND :cellular = 0 AND :vpn = 0 AND :unknown = 0) OR
                (:wifi = 1 AND networkType = 'wifi') OR
                (:cellular = 1 AND networkType = 'cellular') OR
                (:vpn = 1 AND networkType = 'vpn') OR
                (:unknown = 1 AND networkType = 'unknown')
            )
        ORDER BY epochSeconds DESC
    """
    )
    fun observePaged(
        query: String?,
        ipv4: Boolean,
        ipv6: Boolean,
        country: String?,
        wifi: Boolean = false,
        cellular: Boolean = false,
        vpn: Boolean = false,
        unknown: Boolean = false,
    ): PagingSource<Int, AddressHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: AddressHistoryEntity): Long

    @Query("DELETE FROM AddressHistory")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<AddressHistoryEntity>)

    @Query(
        """
        SELECT * 
        FROM AddressHistory
        WHERE
            (:query IS NULL OR address LIKE '%' || :query || '%') AND
            (
                (addressVersion = CASE WHEN :ipv4 THEN ${AddressVersionSQLConstants.IPV4} ELSE -1 END) OR
                (addressVersion = CASE WHEN :ipv6 THEN ${AddressVersionSQLConstants.IPV6} ELSE -1 END)
            ) AND
            (:country IS NULL OR LOWER(country) LIKE '%' || LOWER(:country) || '%') AND
            (
                (:wifi = 0 AND :cellular = 0 AND :vpn = 0 AND :unknown = 0) OR
                (:wifi = 1 AND networkType = 'wifi') OR
                (:cellular = 1 AND networkType = 'cellular') OR
                (:vpn = 1 AND networkType = 'vpn') OR
                (:unknown = 1 AND networkType = 'unknown')
            )
        ORDER BY epochSeconds DESC
        LIMIT 5000
    """
    )
    suspend fun getFilteredList(
        query: String?,
        ipv4: Boolean,
        ipv6: Boolean,
        country: String?,
        wifi: Boolean = false,
        cellular: Boolean = false,
        vpn: Boolean = false,
        unknown: Boolean = false,
    ): List<AddressHistoryEntity>

    @Query(
        """
        SELECT * 
        FROM AddressHistory
        WHERE addressVersion = :version
        ORDER BY epochSeconds DESC
        LIMIT 1
    """
    )
    suspend fun getLatestAddress(version: AddressVersion): AddressHistoryEntity?
}