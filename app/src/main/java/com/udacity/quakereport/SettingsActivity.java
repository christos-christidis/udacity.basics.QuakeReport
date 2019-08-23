package com.udacity.quakereport;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.min_magnitude_pref_key));
            showValueInSummary(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.order_by_pref_key));
            showValueInSummary(orderBy);
        }

        // SOS: by default, options don't show any summary! So I have to call this and also set the
        // changeListener so I can update the summary when a setting changes.
        private void showValueInSummary(Preference preference) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String value = prefs.getString(preference.getKey(), "");
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                value = getOrderByLabelFromValue(listPreference, value);
            }
            preference.setSummary(value);
            preference.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String newValueString = (String) newValue;
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                String label = getOrderByLabelFromValue(listPreference, newValueString);
                listPreference.setSummary(label);
            } else {
                preference.setSummary(newValueString);
            }

            return true;
        }

        private String getOrderByLabelFromValue(ListPreference preference, String value) {
            int indexOfValue = preference.findIndexOfValue(value);
            CharSequence[] labels = preference.getEntries();
            return labels[indexOfValue].toString();
        }
    }
}
