package com.yourpackage.discovery

enum class Medium {
    BLE,
    WIFI
}

data class Peer(
    val ephemeralId: String,
    val lastSeenAt: Long,
    val medium: Medium
)
