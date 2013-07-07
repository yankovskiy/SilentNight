package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.preference.Preference.OnPreferenceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class MainActivity extends PreferenceActivity {

    private CheckBoxPreference mIsServiceEnabled;
    private TimePreference mSilentModeEndAt;
    private TimePreference mSilentModeStartAt;
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        mIsServiceEnabled = (CheckBoxPreference) findPreference(Constant.PREF_IS_SERVICE_ENABLED);
        mSilentModeStartAt = (TimePreference) findPreference(Constant.PREF_SILENT_MODE_START_AT);
        mSilentModeEndAt = (TimePreference) findPreference(Constant.PREF_SILENT_MODE_END_AT);
        updateView();
        
        mIsServiceEnabled.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                updateView();
                serviceControl();
                return false;
            }
        });
    }

    
    private void updateView() {
        Log.message("updatePrefs");
        mSilentModeEndAt.setEnabled(!mIsServiceEnabled.isChecked());
        mSilentModeStartAt.setEnabled(!mIsServiceEnabled.isChecked());
    }
    
    private void serviceControl() {
        Log.message("serviceControl");
        if (mIsServiceEnabled.isChecked()) {
            startService(new Intent(this, SilentNightService.class));
        } else {
            stopService(new Intent(this, SilentNightService.class));
        }
    }

}
