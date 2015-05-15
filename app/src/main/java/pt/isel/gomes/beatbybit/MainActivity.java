package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import pt.isel.gomes.beatbybit.util.Engine;

import static com.dropbox.client2.DropboxAPI.Entry;



public class MainActivity extends Activity {
    private Engine engine;
    final static private String APP_KEY = "un624qhagsgq8wb";
    final static private String APP_SECRET = "wid188gkonsbj62";
    private DropboxAPI<AndroidAuthSession> dropbox;
    SharedPreferences sharedpreferences;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        engine = new Engine();

        sharedpreferences = getSharedPreferences("beat", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken",null);
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;
        if(accessToken != null){
            session = new AndroidAuthSession(appKeys,accessToken);
        }
        else {
            session = new AndroidAuthSession(appKeys);
        }
        dropbox = new DropboxAPI<AndroidAuthSession>(session);
        dropbox.getSession().startOAuth2Authentication(MainActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
    public void launchRec(View view){
        Intent intent = new Intent(this, Rec.class);
        intent.putExtra("engine",engine);
        startActivity(intent);
    }
    public void launchCooper(View view){
        Intent intent = new Intent(this, Cooper.class);
        intent.putExtra("engine",engine);
        startActivity(intent);
    }
    public void launchAct(View view){
        Intent intent = new Intent(this, Activities.class);
        intent.putExtra("engine",engine);
        startActivity(intent);
    }
    public void launchSettings(View view) throws IOException, DropboxException {
        //Intent intent = new Intent(this, Settings.class);
       //intent.putExtra("engine",engine);
        //startActivity(intent);
        File MYDIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File beatByBIT = new File(MYDIR, "beatByBIT"); // TODO IMPROVE
        File f = new File(beatByBIT,"myText.txt" );
        f.createNewFile();
        FileInputStream inputStream = new FileInputStream(f);
        Entry response = dropbox.putFile("/magnum-opus.txt", inputStream,f.length(), null, null);
        Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
    }

    protected void onResume() {
        super.onResume();
        if (dropbox.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                sharedpreferences = getSharedPreferences("beat", Context.MODE_PRIVATE);
                dropbox.getSession().finishAuthentication();
                accessToken = dropbox.getSession().getOAuth2AccessToken();
                sharedpreferences.edit().putString("accessToken", accessToken);
                sharedpreferences.edit().commit();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
}
