package app.callgate.android.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.toSpanned
import androidx.fragment.app.Fragment
import app.callgate.android.R
import app.callgate.android.databinding.FragmentHomeBinding
import app.callgate.android.modules.connection.ConnectionViewModel
import app.callgate.android.modules.orchestrator.OrchestratorService
import app.callgate.android.modules.server.ServerService
import app.callgate.android.modules.server.ServerViewModel
import app.callgate.android.ui.settings.SettingsFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val orchestratorSvc: OrchestratorService by inject()
    private val serverSvc: ServerService by inject()

    private val connectionVM: ConnectionViewModel by viewModel()
    private val serverVM: ServerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionVM.localIP.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.textLocalIP.setCopyableText("$it:${serverSvc.settings.port}")
            } else {
                binding.textLocalIP.text = getString(R.string.n_a)
            }
        }

        connectionVM.publicIP.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.textPublicIP.setCopyableText("$it:${serverSvc.settings.port}")
            } else {
                binding.textPublicIP.text = getString(R.string.n_a)
            }
        }

        serverVM.credentials.observe(viewLifecycleOwner) {
            binding.textLocalUsername.setCopyableText(it.username)
            binding.textLocalPassword.setCopyableText(it.password)
        }

        serverSvc.isActiveLiveData().observe(viewLifecycleOwner) {
            binding.toggleOnline.isChecked = it
        }

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
    }

    override fun onResume() {
        super.onResume()

        connectionVM.getLocalIP()
        connectionVM.getPublicIP()
        serverVM.refresh()
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
        orchestratorSvc.start(requireContext(), autostart = false)
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

    private fun TextView.setCopyableText(text: String) {
        val source = Html.fromHtml("<a href>$text</a>")
        val builder = SpannableStringBuilder(source)
        val spans = builder.getSpans(0, builder.length, URLSpan::class.java)
        for (span in spans) {
            val innerText = builder.subSequence(
                builder.getSpanStart(span),
                builder.getSpanEnd(span)
            ).toString()
            val clickableSpan = object : ClickableSpan() {

                override fun onClick(widget: View) {
                    try {
                        val clipboard = requireContext().getSystemService(
                            Context.CLIPBOARD_SERVICE
                        ) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("", innerText))
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            Toast.makeText(
                                context,
                                R.string.copied_to_clipboard,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Failed to copy text to clipboard", e)
                        Toast.makeText(
                            context,
                            context.getString(R.string.failed_to_copy_to_clipboard),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            builder.setSpan(
                clickableSpan,
                builder.getSpanStart(span),
                builder.getSpanEnd(span),
                builder.getSpanFlags(span)
            )
            builder.removeSpan(span)
        }

        this.movementMethod = LinkMovementMethod.getInstance()
        this.text = builder.toSpanned()
    }

    companion object {
        fun newInstance() =
            HomeFragment()
    }
}