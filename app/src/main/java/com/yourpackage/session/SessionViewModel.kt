package com.yourpackage.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow

class SessionViewModel : ViewModel() {

    private val sessionManager = SessionManager(viewModelScope)

    val sessionState: StateFlow<SessionState> = sessionManager.sessionState

    fun sendInvite(toEphemeralId: String, action: InviteAction) {
        sessionManager.sendInvite(toEphemeralId, action)
    }

    fun respondToInvite(fromEphemeralId: String, response: InviteResponse) {
        sessionManager.respondToInvite(fromEphemeralId, response)
    }
    
    init {
        // In a real app, you would have a network layer that receives invites.
        // For this example, we'll simulate an incoming invite after 5 seconds.
        viewModelScope.launch {
            kotlinx.coroutines.delay(5000)
            val simulatedInvite = Invite("peer_123", "my_ephemeral_id", InviteAction.INVITE)
            sessionManager.handleIncomingInvite(simulatedInvite)
        }
    }
}
