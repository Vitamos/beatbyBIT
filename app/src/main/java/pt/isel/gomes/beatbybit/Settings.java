package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

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
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference);
        }
    }
}

