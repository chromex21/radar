package com.yourpackage.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.yourpackage.R
import com.yourpackage.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collect

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()

        lifecycleScope.launchWhenCreated {
            viewModel.settings.collect { settings ->
                updateUi(settings)
            }
        }

        setupListeners()
    }

    private fun setupSpinners() {
        binding.rotationSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            EphemeralIdRotationInterval.values().map { "${it.minutes} minutes" }
        )

        binding.ttlSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ChatAutoDeleteTtl.values().map { "${it.hours} hours" }
        )
    }

    private fun updateUi(settings: PrivacySettings) {
        when (settings.availability) {
            Availability.AVAILABLE -> binding.availabilityGroup.check(R.id.available_radio)
            Availability.BUSY -> binding.availabilityGroup.check(R.id.busy_radio)
            Availability.INVISIBLE -> binding.availabilityGroup.check(R.id.invisible_radio)
        }

        binding.bleDiscoverySwitch.isChecked = settings.isBleDiscoveryEnabled
        binding.wifiDiscoverySwitch.isChecked = settings.isWifiDiscoveryEnabled

        binding.rotationSpinner.setSelection(settings.ephemeralIdRotationInterval.ordinal)
        binding.ttlSpinner.setSelection(settings.chatAutoDeleteTtl.ordinal)

        binding.revealConsentSwitch.isChecked = settings.isRevealConsentEnabled
    }

    private fun setupListeners() {
        binding.availabilityGroup.setOnCheckedChangeListener { _, checkedId ->
            val availability = when (checkedId) {
                R.id.available_radio -> Availability.AVAILABLE
                R.id.busy_radio -> Availability.BUSY
                R.id.invisible_radio -> Availability.INVISIBLE
                else -> return@setOnCheckedChangeListener
            }
            viewModel.updateSettings(viewModel.settings.value.copy(availability = availability))
        }

        binding.bleDiscoverySwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateSettings(viewModel.settings.value.copy(isBleDiscoveryEnabled = isChecked))
        }

        binding.wifiDiscoverySwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateSettings(viewModel.settings.value.copy(isWifiDiscoveryEnabled = isChecked))
        }
        
        // Add listeners for spinners and reveal consent switch
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
