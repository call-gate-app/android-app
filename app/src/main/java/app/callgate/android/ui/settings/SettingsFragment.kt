package app.callgate.android.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import app.callgate.android.BuildConfig
import app.callgate.android.R

class SettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("transient.app_version")?.summary =
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}