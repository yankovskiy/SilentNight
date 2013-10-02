package ru.neverdark.silentnight;

import ru.neverdark.log.Log;
import android.preference.Preference.OnPreferenceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Service control activity
 */
public class MainActivity extends PreferenceActivity {

    private Preference mContactDeveloper;
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
        mContactDeveloper = findPreference(Constant.PREF_CONTACT_DEVELOPER);
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
