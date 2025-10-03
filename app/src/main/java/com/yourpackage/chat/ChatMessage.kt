package com.yourpackage.chat

sealed class ChatMessage {
    abstract val id: String
    abstract val timestamp: Long
    abstract val senderId: String

    data class TextMessage(
        override val id: String,
        override val timestamp: Long,
        override val senderId: String,
        val text: String
    ) : ChatMessage()

    data class ImageMessage(
        override val id: String,
        override val timestamp: Long,
        override val senderId: String,
        val imageUrl: String,
        val progress: Int? = null // For upload/download progress
    ) : ChatMessage()

    data class AudioMessage(
        override val id: String,
        override val timestamp: Long,
        override val senderId: String,
        val audioUrl: String,
        val duration: Int, // in seconds
        val isPlaying: Boolean = false
    ) : ChatMessage()

    data class StickerMessage(
        override val id: String,
        override val timestamp: Long,
        override val senderId: String,
        val stickerUrl: String
    ) : ChatMessage()
}
