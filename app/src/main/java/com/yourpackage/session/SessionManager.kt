package com.yourpackage.session

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionManager(private val externalScope: CoroutineScope) {

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    // This is a placeholder for a real network layer
    private var sendInviteCallback: ((Invite) -> Unit)? = null

    fun setSendInviteCallback(callback: (Invite) -> Unit) {
        this.sendInviteCallback = callback
    }

    fun sendInvite(toEphemeralId: String, action: InviteAction) {
        val fromEphemeralId = "my_ephemeral_id" // Replace with actual ephemeral ID
        val invite = Invite(fromEphemeralId, toEphemeralId, action)

        if (isPeerOnCooldown(toEphemeralId)) {
            Log.d(TAG, "Peer $toEphemeralId is on cooldown. Cannot send invite.")
            return
        }

        if (isPeerMuted(toEphemeralId)) {
            Log.d(TAG, "Peer $toEphemeralId is muted. Cannot send invite.")
            return
        }

        sendInviteCallback?.invoke(invite)
        Log.d(TAG, "Sent $action to $toEphemeralId")
    }

    fun handleIncomingInvite(invite: Invite) {
        if (isPeerMuted(invite.fromEphemeralId)) {
            Log.d(TAG, "Ignoring invite from muted peer: ${invite.fromEphemeralId}")
            return
        }

        val currentInvites = _sessionState.value.activeInvites.toMutableMap()
        currentInvites[invite.fromEphemeralId] = invite
        _sessionState.value = _sessionState.value.copy(activeInvites = currentInvites)
        Log.d(TAG, "Handling incoming ${invite.action} from ${invite.fromEphemeralId}")
    }

    fun respondToInvite(fromEphemeralId: String, response: InviteResponse) {
        val currentInvites = _sessionState.value.activeInvites.toMutableMap()
        currentInvites.remove(fromEphemeralId)
        _sessionState.value = _sessionState.value.copy(activeInvites = currentInvites)

        when (response) {
            InviteResponse.ACCEPT -> {
                Log.d(TAG, "Accepted invite from $fromEphemeralId")
                // Here you would navigate to the chat screen
            }
            InviteResponse.DECLINE -> {
                Log.d(TAG, "Declined invite from $fromEphemeralId")
                startCooldown(fromEphemeralId)
            }
            InviteResponse.IGNORE -> {
                Log.d(TAG, "Ignored invite from $fromEphemeralId")
            }
            InviteResponse.MUTE -> {
                Log.d(TAG, "Muted peer $fromEphemeralId")
                mutePeer(fromEphemeralId)
            }
        }
    }

    private fun startCooldown(peerId: String) {
        val cooldownUntil = System.currentTimeMillis() + COOLDOWN_PERIOD
        val currentCooldowns = _sessionState.value.cooldownPeers.toMutableMap()
        currentCooldowns[peerId] = cooldownUntil
        _sessionState.value = _sessionState.value.copy(cooldownPeers = currentCooldowns)
    }

    private fun mutePeer(peerId: String) {
        val currentMuted = _sessionState.value.mutedPeers.toMutableSet()
        currentMuted.add(peerId)
        _sessionState.value = _sessionState.value.copy(mutedPeers = currentMuted)
    }

    private fun isPeerOnCooldown(peerId: String): Boolean {
        val cooldownUntil = _sessionState.value.cooldownPeers[peerId]
        return cooldownUntil != null && System.currentTimeMillis() < cooldownUntil
    }

    private fun isPeerMuted(peerId: String): Boolean {
        return _sessionState.value.mutedPeers.contains(peerId)
    }

    companion object {
        private const val TAG = "SessionManager"
        private const val COOLDOWN_PERIOD = 30000L // 30 seconds
    }
}
