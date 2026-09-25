package com.example.notisaver

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appName: String,
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
    val isOutgoing: Boolean = false
)
