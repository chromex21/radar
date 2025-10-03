package com.yourpackage.ui.invite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.yourpackage.R
import com.yourpackage.databinding.FragmentInviteDialogBinding
import com.yourpackage.session.InviteResponse
import com.yourpackage.session.SessionViewModel

class InviteDialogFragment : DialogFragment() {

    private var _binding: FragmentInviteDialogBinding? = null
    private val binding get() = _binding!!

    private val sessionViewModel: SessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fromEphemeralId = requireArguments().getString(ARG_FROM_EPHEMERAL_ID)
        binding.inviteMessage.text = "Invite from: $fromEphemeralId"

        binding.acceptButton.setOnClickListener {
            sessionViewModel.respondToInvite(fromEphemeralId!!, InviteResponse.ACCEPT)
            dismiss()
        }

        binding.declineButton.setOnClickListener {
            sessionViewModel.respondToInvite(fromEphemeralId!!, InviteResponse.DECLINE)
            dismiss()
        }

        binding.ignoreButton.setOnClickListener {
            sessionViewModel.respondToInvite(fromEphemeralId!!, InviteResponse.IGNORE)
            dismiss()
        }

        binding.muteButton.setOnClickListener {
            sessionViewModel.respondToInvite(fromEphemeralId!!, InviteResponse.MUTE)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "InviteDialogFragment"
        private const val ARG_FROM_EPHEMERAL_ID = "from_ephemeral_id"

        fun newInstance(fromEphemeralId: String) = InviteDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_FROM_EPHEMERAL_ID, fromEphemeralId)
            }
        }
    }
}
