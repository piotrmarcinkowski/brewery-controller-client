package com.pma.bcc.fragments


import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.preference.*
import com.pma.bcc.R
import com.pma.bcc.viewmodels.SettingsViewModel
import com.pma.bcc.viewmodels.ViewModelFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var model: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // manually inject because this class doesn't inherit DaggerFragment
        AndroidSupportInjection.inject(this)

        model = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val serverStatusPreference: Preference? = findPreference("prefs_connection_status")
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        initSummaries(preferenceScreen)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        model.onSettingsChanged()
        initSummaries(preferenceScreen)
    }

    private fun initSummaries(p: Preference) {
        when (p) {
            is PreferenceGroup -> {
                for (i in 0 until p.preferenceCount) {
                    initSummaries(p.getPreference(i))
                }
            }
            is ListPreference -> {
                p.setSummary(p.entry)
            }
            is EditTextPreference -> {
                p.setSummary(p.text)
            }
        }
    }
}
