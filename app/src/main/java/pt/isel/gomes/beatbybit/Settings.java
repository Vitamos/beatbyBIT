package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import pt.isel.gomes.beatbybit.util.Engine;


public class Settings extends Activity {
    private static Engine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        engine = (Engine) getIntent().getSerializableExtra("engine");
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {
        private SharedPreferences.Editor prefEdit;
        private SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefEdit = prefs.edit();

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            EditTextPreference mac = (EditTextPreference) getPreferenceScreen().findPreference("mac_preference");
            mac.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean rtnval = true;
                    String address = (String) newValue;
                    Log.i("ADD ", address);
                    if (!engine.setMac(address)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Invalid Input");
                        builder.setMessage("Invalid MAC Address...");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();
                        rtnval = false;
                    }
                    return rtnval;
                }
            });
            Preference dirButton = findPreference("dirbutton");
            dirButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(engine.getRootDir().getPath());
                    intent.setDataAndType(uri, "resource/folder");
                    startActivity(Intent.createChooser(intent, "Open folder"));
                    return true;
                }
            });
            Preference dropButton = findPreference("dropbutton");
            dropButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    prefEdit.putBoolean("prefDrop", true);
                                    DropboxAPI<AndroidAuthSession> dropbox = engine.getDropboxAPI(getActivity());
                                    if (dropbox.getSession().authenticationSuccessful()) {
                                        try {
                                            // Required to complete auth, sets the access token on the session
                                            dropbox.getSession().finishAuthentication();
                                            String token = dropbox.getSession().getOAuth2AccessToken();
                                            prefEdit.putString("token", token);
                                            prefEdit.apply();
                                        } catch (IllegalStateException e) {
                                            Log.i("DbAuthLog", "Error authenticating", e);
                                        }
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    prefEdit.putBoolean("prefDrop", false);
                                    break;
                            }
                            prefEdit.commit();
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want to sync with Dropbox?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

                    return true;
                }
            });
        }
    }
}

