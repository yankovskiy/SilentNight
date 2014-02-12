/*******************************************************************************
 * Copyright (C) 2014 Grégory Soutadé.
 * Copyright (C) 2013-2014 Artem Yankovskiy (artemyankovskiy@gmail.com).
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.preference.Preference.OnPreferenceClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import eu.chainfire.libsuperuser.Shell;

/**
 * Service control activity
 */
public class MainActivity extends PreferenceActivity {

    private Preference mContactDeveloper;
    private Preference mRate;
    private CheckBoxPreference mIsServiceEnabled;
    private TimePreference mSilentModeEndAt;
    private TimePreference mSilentModeStartAt;
    private CheckBoxPreference mSuMode;
    private CheckBoxPreference mAirplaneMode;

    public static class SettingsFragment extends PreferenceFragment {
        private MainActivity mOuter;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);
            mOuter.loadUI();
            mOuter.updateView();
            mOuter.setPreferencesClickListener();
        }

        public void setOuter(MainActivity outer) {
            mOuter = outer;
        }

        public CheckBoxPreference getServiceEnabled() {
            return (CheckBoxPreference) findPreference(Constant.PREF_IS_SERVICE_ENABLED);
        }

        public TimePreference getSilentModeStart() {
            return (TimePreference) findPreference(Constant.PREF_SILENT_MODE_START_AT);
        }

        public TimePreference getSilentModeEnd() {
            return (TimePreference) findPreference(Constant.PREF_SILENT_MODE_END_AT);
        }

        public Preference getContact() {
            return findPreference(Constant.PREF_CONTACT_DEVELOPER);
        }

        public Preference getRate() {
            return findPreference(Constant.PREF_RATE);
        }

        public CheckBoxPreference getSuMode() {
            return (CheckBoxPreference) findPreference(Constant.PREF_SU_MODE);
        }

        public CheckBoxPreference getDisableSound() {
            return (CheckBoxPreference) findPreference(Constant.PREF_DISABLE_SOUND);
        }

        public CheckBoxPreference getAirplaneMode() {
            return (CheckBoxPreference) findPreference(Constant.PREF_AIRPLANE_MODE);
        }
    }

    private SettingsFragment mSettings;

    /**
     * Opens market detail application page
     */
    private void gotoMarket() {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
        marketIntent.setData(Uri
                .parse("market://details?id=ru.neverdark.silentnight"));
        startActivity(marketIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        mSettings = new SettingsFragment();
        mSettings.setOuter(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mSettings).commit();
    }

    /**
     * Starts or stops the service, depending on the state of the
     * isServiceEnabled checkbox
     */
    private void serviceControl() {
        Log.message("serviceControl");
        startService(new Intent(this, SilentNightService.class));
    }

    /**
     * Sets preferences listener for handle onClick event
     */
    private void setPreferencesClickListener() {
        mIsServiceEnabled
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        updateView();
                        serviceControl();
                        return false;
                    }
                });

        mContactDeveloper
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        showContactDeveloperIntent();
                        return false;
                    }
                });

        mRate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                gotoMarket();
                return false;
            }
        });

        mSuMode.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                updateView();
                return false;
            }
        });
    }

    /**
     * Shows select email application dialog
     */
    private void showContactDeveloperIntent() {
        Intent mailto = new Intent(Intent.ACTION_SEND);
        mailto.setType("plain/text");
        mailto.putExtra(Intent.EXTRA_EMAIL,
                new String[] { getString(R.string.pref_email) });
        mailto.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        startActivity(Intent.createChooser(mailto,
                getString(R.string.pref_selectEmailApplication)));
    }

    /**
     * Load UI from preferences
     */
    private void loadUI() {
        mIsServiceEnabled = mSettings.getServiceEnabled();
        mSilentModeStartAt = mSettings.getSilentModeStart();
        mSilentModeEndAt = mSettings.getSilentModeEnd();
        mContactDeveloper = mSettings.getContact();
        mRate = mSettings.getRate();
        mSuMode = mSettings.getSuMode();
        mAirplaneMode = mSettings.getAirplaneMode();

        // if Android less 4.2 or SU does not available
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1
                || !Shell.SU.available()) {
            mSuMode.setEnabled(false);
        }
    }

    /**
     * Updates view to disable / enable the components depending on the state of
     * the isServiceEnabled checkbox
     */
    private void updateView() {
        boolean enabled = !mIsServiceEnabled.isChecked();
        mSilentModeEndAt.setEnabled(enabled);
        mSilentModeStartAt.setEnabled(enabled);

        // if SU mode is not enabled and we have android 4.2+
        // disable airplane mode
        if (!mSuMode.isChecked()
                && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mAirplaneMode.setEnabled(false);
            mAirplaneMode.setChecked(false);
        } else
            mAirplaneMode.setEnabled(true);
    }
}
