package app.callgate.android.ui.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import app.callgate.android.R

class WebhooksSettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.webhooks_preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference.key) {
            "webhooks.retry_count" -> {
                (preference as EditTextPreference).setOnBindEditTextListener {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    configureEditText(it)
                }
            }

            "webhooks.signing_key" -> {
                (preference as EditTextPreference).setOnBindEditTextListener {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    configureEditText(it)
                }
            }
        }

        super.onDisplayPreferenceDialog(preference)
    }
}