package com.yourpackage.settings

enum class Availability {
    AVAILABLE,
    BUSY,
    INVISIBLE
}

enum class EphemeralIdRotationInterval(val minutes: Int) {
    THREE(3),
    FIVE(5),
    TEN(10)
}

enum class ChatAutoDeleteTtl(val hours: Int) {
    ONE(1),
    TWENTY_FOUR(24),
    ONE_HUNDRED_SIXTY_EIGHT(168) // 7 days
}

data class PrivacySettings(
    val availability: Availability = Availability.AVAILABLE,
    val isBleDiscoveryEnabled: Boolean = true,
    val isWifiDiscoveryEnabled: Boolean = true,
    val ephemeralIdRotationInterval: EphemeralIdRotationInterval = EphemeralIdRotationInterval.FIVE,
    val chatAutoDeleteTtl: ChatAutoDeleteTtl = ChatAutoDeleteTtl.TWENTY_FOUR,
    val isRevealConsentEnabled: Boolean = true,
    val blocklist: Set<String> = emptySet(),
    val mutelist: Set<String> = emptySet()
)
