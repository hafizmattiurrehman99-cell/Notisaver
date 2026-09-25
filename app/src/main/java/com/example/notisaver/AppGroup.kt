package com.example.notisaver

/** Summary row: one app, with how many notifications it has and the latest time. */
data class AppGroup(
    val packageName: String,
    val appName: String,
    val count: Int,
    val lastTimestamp: Long
)
