package com.yourpackage

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yourpackage.discovery.DiscoveryViewModel
import com.yourpackage.session.InviteAction
import com.yourpackage.session.SessionViewModel
import com.yourpackage.ui.invite.InviteDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RadarActivity : AppCompatActivity() {

    private val discoveryViewModel: DiscoveryViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radar)

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        lifecycleScope.launch {
            discoveryViewModel.peers.collect { peers ->
                Log.d(TAG, "Discovered peers: $peers")
                // Update your RadarView with the new peer data
            }
        }

        lifecycleScope.launch {
            sessionViewModel.sessionState.collect { state ->
                state.activeInvites.values.firstOrNull()?.let {
                    InviteDialogFragment.newInstance(it.fromEphemeralId)
                        .show(supportFragmentManager, InviteDialogFragment.TAG)
                }
            }
        }\n
        // Example of how to send an invite when a peer is tapped in the RadarView
        // radarView.setOnPeerTapListener { peer ->
        //     sessionViewModel.sendInvite(peer.ephemeralId, InviteAction.INVITE)
        // }
    }

    companion object {
        private const val TAG = "RadarActivity"
    }
}
