package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.io.File;

import pt.isel.gomes.beatbybit.util.Engine;


public class Settings extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Engine engine = (Engine) getIntent().getSerializableExtra("engine");
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();


    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            EditTextPreference mac = (EditTextPreference) getPreferenceScreen().findPreference("mac_preference");
            mac.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                String pattern = "^([0-9a-f]{2}[:-]){5}([0-9a-f]{2})$";
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean rtnval = true;
                    String address = (String) newValue;
                    Log.i("ADD ", address);
                    if (!address.matches(pattern)) {
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
            Preference dirButton = (Preference) findPreference("dirbutton");
            dirButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                            + "/beat/");
                    intent.setDataAndType(uri, "resource/folder");
                    startActivity(Intent.createChooser(intent, "Open folder"));
                    return true;
                }
            });
        }
    }
}

