package com.yourpackage.ui.chat

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.chat.ChatManager
import com.yourpackage.chat.ChatMessage
import com.yourpackage.chat.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatManager = ChatManager(application.applicationContext, viewModelScope)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: Flow<List<ChatMessage>> = _messages

    val connectionState: Flow<ConnectionState>?
        get() = chatManager.getActiveConnection()?.connectionState

    fun establishConnection(peerId: String) {
        chatManager.establishConnection(peerId)
        viewModelScope.launch {
            chatManager.getActiveConnection()?.incomingMessages?.collect { message ->
                val currentMessages = _messages.value.toMutableList()
                currentMessages.add(message)
                _messages.value = currentMessages
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val message = ChatMessage.TextMessage(
                id = System.currentTimeMillis().toString(),
                timestamp = System.currentTimeMillis(),
                senderId = "my_id", // Replace with actual user ID
                text = text
            )
            chatManager.getActiveConnection()?.sendMessage(message)
        }
    }

    fun sendImage(imageUri: Uri) {
        viewModelScope.launch {
            val message = ChatMessage.ImageMessage(
                id = System.currentTimeMillis().toString(),
                timestamp = System.currentTimeMillis(),
                senderId = "my_id", // Replace with actual user ID
                imageUrl = imageUri.toString()
            )
            chatManager.getActiveConnection()?.sendMessage(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatManager.closeConnection()
    }
}
