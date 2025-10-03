package com.yourpackage.session

enum class InviteAction {
    POKE,
    PING,
    INVITE
}

enum class InviteResponse {
    ACCEPT,
    DECLINE,
    IGNORE,
    MUTE
}

data class Invite(
    val fromEphemeralId: String,
    val toEphemeralId: String,
    val action: InviteAction,
    val timestamp: Long = System.currentTimeMillis()
)

data class SessionState(
    val activeInvites: Map<String, Invite> = emptyMap(),
    val mutedPeers: Set<String> = emptySet(),
    val cooldownPeers: Map<String, Long> = emptyMap() // Peer ID to cooldown end time
)
