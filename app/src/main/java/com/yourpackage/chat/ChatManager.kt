package com.yourpackage.chat

import android.content.Context
import kotlinx.coroutines.CoroutineScope

class ChatManager(private val context: Context, private val externalScope: CoroutineScope) {

    private var activeConnection: ChatConnection? = null

    fun establishConnection(peerId: String) {
        activeConnection = if (isWifiAvailable()) {
            WifiChatConnection()
        } else {
            BleChatConnection()
        }
    }

    fun getActiveConnection(): ChatConnection? {
        return activeConnection
    }

    private fun isWifiAvailable(): Boolean {
        // In a real app, you would check for Wi-Fi Direct or a common network.
        return true
    }

    fun closeConnection() {
        activeConnection?.close()
        activeConnection = null
    }
}
