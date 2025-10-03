package com.yourpackage.discovery.wifi

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.MulticastSocket
import java.net.InetAddress
import java.net.DatagramPacket

class WifiListener {

    fun startListening(): Flow<String> = flow {
        val socket = MulticastSocket(PORT)
        val group = InetAddress.getByName(MULTICAST_GROUP)
        socket.joinGroup(group)

        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)

        while (true) {
            socket.receive(packet)
            val ephemeralId = String(packet.data, 0, packet.length)
            emit(ephemeralId)
            Log.d(TAG, "Received UDP broadcast with ephemeral ID: $ephemeralId")
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val TAG = "WifiListener"
        private const val MULTICAST_GROUP = "239.255.255.250"
        private const val PORT = 1900
    }
}
