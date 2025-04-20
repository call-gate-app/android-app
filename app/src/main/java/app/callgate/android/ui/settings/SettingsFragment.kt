package app.callgate.android.ui.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import app.callgate.android.BuildConfig
import app.callgate.android.R

class SettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("navigation.server")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settings_to_server_settings)
            true
        }

        findPreference<Preference>("navigation.webhooks")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settings_to_webhooks_settings)
            true
        }

        findPreference<Preference>("transient.app_version")?.summary =
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}