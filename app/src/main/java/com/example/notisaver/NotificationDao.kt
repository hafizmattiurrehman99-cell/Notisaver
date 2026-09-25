package com.example.notisaver

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAll(): LiveData<List<NotificationEntity>>

    // One row per app, with a running count and the time of its most recent notification.
    @Query("""
        SELECT packageName, appName, COUNT(*) as count, MAX(timestamp) as lastTimestamp
        FROM notifications
        GROUP BY packageName
        ORDER BY lastTimestamp DESC
    """)
    fun getAppGroups(): LiveData<List<AppGroup>>

    // One row per sender/title within a given app, with a preview of their latest message.
    @Query("""
        SELECT title,
               COUNT(*) as count,
               MAX(timestamp) as lastTimestamp,
               (SELECT text FROM notifications n2
                WHERE n2.packageName = :packageName AND n2.title = notifications.title
                ORDER BY timestamp DESC LIMIT 1) as lastText
        FROM notifications
        WHERE packageName = :packageName
        GROUP BY title
        ORDER BY lastTimestamp DESC
    """)
    fun getSenderGroups(packageName: String): LiveData<List<SenderGroup>>

    // Full conversation with one sender, oldest first (like a chat script).
    @Query("""
        SELECT * FROM notifications
        WHERE packageName = :packageName AND title = :title
        ORDER BY timestamp ASC
    """)
    fun getMessagesForSender(packageName: String, title: String): LiveData<List<NotificationEntity>>
}
