package com.yourpackage.discovery.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class BleScanner(private val bluetoothAdapter: BluetoothAdapter) {

    private val scanner: BluetoothLeScanner? = bluetoothAdapter.bluetoothLeScanner
    private val serviceUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Same UUID as advertiser

    fun startScanning(): Flow<String> = callbackFlow {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val serviceData = result.scanRecord?.getServiceData(ParcelUuid(serviceUuid))
                if (serviceData != null) {
                    val ephemeralId = String(serviceData)
                    trySend(ephemeralId)
                    Log.d(TAG, "Discovered peer via BLE: $ephemeralId")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "BLE scan failed with error code: $errorCode")
            }
        }

        val scanFilters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(serviceUuid))
                .build()
        )

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(scanFilters, scanSettings, scanCallback)

        awaitClose { scanner?.stopScan(scanCallback) }
    }

    companion object {
        private const val TAG = "BleScanner"
    }
}
