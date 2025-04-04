package app.callgate.android.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.callgate.android.R
import app.callgate.android.databinding.FragmentHomeBinding
import app.callgate.android.modules.orchestrator.OrchestratorService
import app.callgate.android.modules.server.ServerService
import app.callgate.android.ui.settings.SettingsFragment
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val orchestratorSvc: OrchestratorService by inject()
    private val serverSvc: ServerService by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionSettings.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, SettingsFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        binding.toggleOnline.setOnClickListener {
            actionStart(binding.toggleOnline.isChecked)
        }

        serverSvc.isActiveLiveData().observe(viewLifecycleOwner) {
            binding.toggleOnline.isChecked = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun actionStart(start: Boolean) {
        if (start) {
            requestPermissionsAndStart()
        } else {
            stop()
        }
    }

    private fun start() {
        orchestratorSvc.start(requireContext(), false)
    }

    private fun stop() {
        orchestratorSvc.stop(requireContext())
    }

    private fun requestPermissionsAndStart() {
        val permissionsRequired = listOfNotNull(
            Manifest.permission.ANSWER_PHONE_CALLS.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.O },
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
        ).filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsRequired.isEmpty()) {
            start()
            return
        }

        permissionsRequest.launch(permissionsRequired.toTypedArray())
    }

    private val permissionsRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            Log.d(javaClass.name, "Permissions granted")
            start()
        } else {
            Toast.makeText(requireContext(), "Not all permissions granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        fun newInstance() =
            HomeFragment()
    }
}