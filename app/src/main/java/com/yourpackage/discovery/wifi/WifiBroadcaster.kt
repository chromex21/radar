package com.yourpackage.discovery.wifi

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class WifiBroadcaster {

    private var broadcastJob: Job? = null

    fun startBroadcasting(ephemeralId: String) {
        broadcastJob = CoroutineScope(Dispatchers.IO).launch {
            val socket = DatagramSocket()
            val address = InetAddress.getByName(MULTICAST_GROUP)
            while (true) {
                val buffer = ephemeralId.toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, address, PORT)
                socket.send(packet)
                Log.d(TAG, "Sent UDP broadcast with ephemeral ID: $ephemeralId")
                delay(BROADCAST_INTERVAL)
            }
        }
    }

    fun stopBroadcasting() {
        broadcastJob?.cancel()
        broadcastJob = null
        Log.d(TAG, "UDP broadcasting stopped")
    }

    companion object {
        private const val TAG = "WifiBroadcaster"
        private const val MULTICAST_GROUP = "239.255.255.250"
        private const val PORT = 1900
        private const val BROADCAST_INTERVAL = 5000L // 5 seconds
    }
}
