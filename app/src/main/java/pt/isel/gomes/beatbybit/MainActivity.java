package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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



public class MainActivity extends Activity {
    private Engine engine;
    final static private String APP_KEY = "un624qhagsgq8wb";
    final static private String APP_SECRET = "wid188gkonsbj62";
    private DropboxAPI<AndroidAuthSession> dropbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dropbox = getDropboxAPI();
        engine = new Engine();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private DropboxAPI <AndroidAuthSession> getDropboxAPI(){
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        DropboxAPI<AndroidAuthSession> mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedpreferences.getString("token",null);
        if (token == null){
            mDBApi.getSession().startOAuth2Authentication(this);
            return mDBApi;
        }
        mDBApi.getSession().setOAuth2AccessToken(token);
        return mDBApi;
    }

    public void storePrefs(String token){
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token", token);
        editor.commit();
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



    public void launchSettings(View view) throws IOException{
        Intent intent = new Intent(this, Settings.class);
        intent.putExtra("engine",engine);
        startActivity(intent);


    }
    public void testCloud() throws IOException, DropboxException

    {
        File file = new File("myText.txt");

        file.createNewFile();
        engine.writeToFile("myText.txt","teste");
        FileInputStream inputStream = new FileInputStream(file);
        DropboxAPI.Entry response = dropbox.putFile("/magnum-opus.txt", inputStream,
                file.length(), null, null);}

    protected void onResume() {
        super.onResume();
        if (dropbox.getSession().authenticationSuccessful())  {
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
