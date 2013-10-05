/*******************************************************************************
 * Copyright (C) 2013 Artem Yankovskiy (artemyankovskiy@gmail.com).
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
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Service control activity
 */
public class MainActivity extends PreferenceActivity {

    private Preference mContactDeveloper;
    private Preference mRate;
    private CheckBoxPreference mIsServiceEnabled;
    private TimePreference mSilentModeEndAt;
    private TimePreference mSilentModeStartAt;

    /**
     * Opens market detail application page
     */
    private void gotoMarket() {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
        marketIntent.setData(Uri
                .parse("market://details?id=ru.neverdark.silentnight"));
        startActivity(marketIntent);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        mIsServiceEnabled = (CheckBoxPreference) findPreference(Constant.PREF_IS_SERVICE_ENABLED);
        mSilentModeStartAt = (TimePreference) findPreference(Constant.PREF_SILENT_MODE_START_AT);
        mSilentModeEndAt = (TimePreference) findPreference(Constant.PREF_SILENT_MODE_END_AT);
        mContactDeveloper = findPreference(Constant.PREF_CONTACT_DEVELOPER);
        mRate = findPreference(Constant.PREF_RATE);
        updateView();
        setPreferencesClickListener();
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
     * Updates view to disable / enable the components depending on the state of
     * the isServiceEnabled checkbox
     */
    private void updateView() {
        boolean enabled = !mIsServiceEnabled.isChecked();
        mSilentModeEndAt.setEnabled(enabled);
        mSilentModeStartAt.setEnabled(enabled);
    }

}
