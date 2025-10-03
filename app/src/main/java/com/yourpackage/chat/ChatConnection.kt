package com.yourpackage.chat

import kotlinx.coroutines.flow.Flow

enum class ConnectionState {
    CONNECTED,
    CONNECTING,
    RECONNECTING,
    DISCONNECTED,
    EXPIRED
}

interface ChatConnection {
    val connectionState: Flow<ConnectionState>
    val incomingMessages: Flow<ChatMessage>

    suspend fun sendMessage(message: ChatMessage)
    suspend fun sendFile(fileUri: String)
    suspend fun startVoiceChat()
    suspend fun stopVoiceChat()
    suspend fun revealIdentity()
    fun close()
}
