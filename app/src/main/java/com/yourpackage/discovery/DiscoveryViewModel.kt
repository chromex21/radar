package com.yourpackage.discovery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.discovery.ble.BleAdvertiser
import com.yourpackage.discovery.ble.BleScanner
import com.yourpackage.discovery.wifi.WifiBroadcaster
import com.yourpackage.discovery.wifi.WifiListener
import kotlinx.coroutines.flow.StateFlow

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {

    private val peerRepository: PeerRepository

    val peers: StateFlow<List<Peer>>

    init {
        val bluetoothAdapter = getApplication<Application>().getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothAdapter
        peerRepository = PeerRepository(
            BleAdvertiser(bluetoothAdapter),
            BleScanner(bluetoothAdapter),
            WifiBroadcaster(),
            WifiListener(),
            viewModelScope
        )
        peers = peerRepository.peerFlow
        peerRepository.startDiscovery()
    }

    fun getEphemeralId(): String = peerRepository.getEphemeralId()

    override fun onCleared() {
        super.onCleared()
        peerRepository.stopDiscovery()
    }
}
