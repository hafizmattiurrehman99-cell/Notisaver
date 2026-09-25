package com.example.notisaver

/** Summary row: one sender/chat within an app, with a preview of their last message. */
data class SenderGroup(
    val title: String,
    val count: Int,
    val lastTimestamp: Long,
    val lastText: String
)
