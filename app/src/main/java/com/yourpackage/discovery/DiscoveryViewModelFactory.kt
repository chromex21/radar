package com.yourpackage.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DiscoveryViewModelFactory(private val peerRepository: PeerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoveryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiscoveryViewModel(peerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
