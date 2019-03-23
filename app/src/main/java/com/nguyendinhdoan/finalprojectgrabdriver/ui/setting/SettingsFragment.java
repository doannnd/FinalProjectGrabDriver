package com.nguyendinhdoan.finalprojectgrabdriver.ui.setting;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import com.nguyendinhdoan.finalprojectgrabdriver.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);

        bindSummaryValue(findPreference(getString(R.string.setting_dialog_key_map_type)));
    }

    private static void bindSummaryValue(Preference preference) {
        preference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        onPreferenceChangeListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String value = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(value);

                preference.setSummary(index > 0 ? listPreference.getEntries()[index] : null);
            } else {
                preference.setSummary(value);
            }
            return true;
        }
    };
}
