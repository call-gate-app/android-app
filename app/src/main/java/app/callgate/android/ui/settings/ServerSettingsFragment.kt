package app.callgate.android.ui.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import app.callgate.android.R

class ServerSettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.server_preferences, rootKey)

        findPreference<EditTextPreference>("localserver.port")?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as? String
            val intValue = value?.toIntOrNull()
            if (intValue == null || intValue < 1024 || intValue > 65535) {
                showToast(
                    getString(
                        R.string.is_not_a_valid_port_must_be_between_1024_and_65535,
                        value
                    )
                )
                return@setOnPreferenceChangeListener false
            }

            true
        }

        findPreference<EditTextPreference>("localserver.username")?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as? String
            if ((value?.length ?: 0) < 3) {
                showToast(getString(R.string.username_must_be_at_least_3_characters))
                return@setOnPreferenceChangeListener false
            }

            true
        }
        findPreference<EditTextPreference>("localserver.password")?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as? String
            if ((value?.length ?: 0) < 8) {
                showToast(getString(R.string.password_must_be_at_least_8_characters))
                return@setOnPreferenceChangeListener false
            }

            true
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference.key == "localserver.port"
        ) {
            (preference as EditTextPreference).setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER
                it.setSelectAllOnFocus(true)
                it.selectAll()
            }
        }

        if (preference.key == "localserver.password"
            || preference.key == "localserver.username"
        ) {
            (preference as EditTextPreference).setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_TEXT
                it.setSelectAllOnFocus(true)
                it.selectAll()
            }
        }

        super.onDisplayPreferenceDialog(preference)
    }
}