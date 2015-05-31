package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import pt.isel.gomes.beatbybit.util.Engine;


public class MainActivity extends Activity {

    private Engine engine;
    private DropboxAPI<AndroidAuthSession> dropbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        engine = new Engine();
        dropbox = engine.getDropboxAPI(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void storePrefs(String token) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public void launchRec(View view) {
        Intent intent = new Intent(this, Rec.class);
        intent.putExtra("engine", engine);
        startActivity(intent);
    }

    public void launchCooper(View view) {
        Intent intent = new Intent(this, Cooper.class);
        intent.putExtra("engine", engine);
        startActivity(intent);
    }

    public void launchAct(View view) {
        Intent intent = new Intent(this, Activities.class);
        intent.putExtra("engine", engine);
        startActivity(intent);
    }

    public void launchSettings(View view) throws IOException, DropboxException {
        //Intent intent = new Intent(this, Settings.class);
        //intent.putExtra("engine", engine);
        //startActivity(intent);
        testCloud();
    }

    public void testCloud() throws IOException, DropboxException {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root, "beat");
        String[] values = engine.getFiles();
        for (String s : values) {
            File f = new File(dir, s);
            FileInputStream inputStream = new FileInputStream(f);
            dropbox.putFile(s, inputStream, f.length(), null, null);
        }
    }


    protected void onResume() {
        super.onResume();
        if (dropbox.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                dropbox.getSession().finishAuthentication();
                String token = dropbox.getSession().getOAuth2AccessToken();
                storePrefs(token);
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
}
