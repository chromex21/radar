package com.yourpackage.discovery

import com.yourpackage.data.storage.SettingsRepository
import com.yourpackage.discovery.ble.BleAdvertiser
import com.yourpackage.discovery.ble.BleScanner
import com.yourpackage.discovery.wifi.WifiBroadcaster
import com.yourpackage.discovery.wifi.WifiListener
import com.yourpackage.settings.Availability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PeerRepository(
    private val bleAdvertiser: BleAdvertiser,
    private val bleScanner: BleScanner,
    private val wifiBroadcaster: WifiBroadcaster,
    private val wifiListener: WifiListener,
    private val settingsRepository: SettingsRepository,
    private val externalScope: CoroutineScope
) {

    private val ephemeralId = UUID.randomUUID().toString()
    private val peers = ConcurrentHashMap<String, Peer>()
    private val _peerFlow = MutableStateFlow<List<Peer>>(emptyList())
    val peerFlow: Flow<List<Peer>> = _peerFlow.asStateFlow()

    private var discoveryJob: Job? = null

    init {
        externalScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                if (settings.availability == Availability.INVISIBLE) {
                    stopDiscovery()
                } else {
                    startDiscovery()
                }
            }
        }

        externalScope.launch {
            while (true) {
                prunePeers()
                delay(PEER_TIMEOUT)
            }
        }
    }

    private fun startDiscovery() {
        if (discoveryJob?.isActive == true) return

        val settings = settingsRepository.settingsFlow.value

        val bleFlow = if (settings.isBleDiscoveryEnabled) bleScanner.startScanning() else kotlinx.coroutines.flow.emptyFlow()
        val wifiFlow = if (settings.isWifiDiscoveryEnabled) wifiListener.startListening() else kotlinx.coroutines.flow.emptyFlow()

        if (settings.isBleDiscoveryEnabled) bleAdvertiser.startAdvertising(ephemeralId)
        if (settings.isWifiDiscoveryEnabled) wifiBroadcaster.startBroadcasting(ephemeralId)

        discoveryJob = merge(bleFlow.map { Peer(it, System.currentTimeMillis(), Medium.BLE) }, wifiFlow.map{ Peer(it, System.currentTimeMillis(), Medium.WIFI) })
            .onEach { peer ->
                if (!settings.blocklist.contains(peer.ephemeralId)) {
                    peers[peer.ephemeralId] = peer
                    _peerFlow.value = peers.values.toList()
                }
            }
            .catch { throwable ->
                // Handle errors
            }
            .launchIn(externalScope)
    }

    fun stopDiscovery() {
        discoveryJob?.cancel()
        bleAdvertiser.stopAdvertising()
        wifiBroadcaster.stopBroadcasting()
    }

    private fun prunePeers() {
        val currentTime = System.currentTimeMillis()
        peers.values.removeAll { peer ->
            (currentTime - peer.lastSeenAt) > PEER_TIMEOUT
        }
        _peerFlow.value = peers.values.toList()
    }

    fun getEphemeralId(): String = ephemeralId

    companion object {
        private const val PEER_TIMEOUT = 30000L // 30 seconds
    }
}
