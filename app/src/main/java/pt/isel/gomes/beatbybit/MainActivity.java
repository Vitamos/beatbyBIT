package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.IOException;

import pt.isel.gomes.beatbybit.util.Engine;
import pt.isel.gomes.beatbybit.util.comm.BITalinoException;
import pt.isel.gomes.beatbybit.util.comm.BITalinoFrame;

public class MainActivity extends Activity {

    private Engine engine;
    private DropboxAPI<AndroidAuthSession> dropbox;
    private SharedPreferences.Editor prefEdit;
    private SharedPreferences prefs;
    private BluetoothAdapter bluetooth;
    private final String PROVIDER_NAME = "com.example.provider.GeneralProvider";
    private final String maleURL = "content://" + PROVIDER_NAME + "/maleTable";
    private final String femaleURL = "content://" + PROVIDER_NAME + "/femaleTable";
    private final Uri maleURI = Uri.parse(maleURL);
    private final Uri femaleURI = Uri.parse(femaleURL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        engine = new Engine();
        createCooperTable();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();
        checkDrop();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        engine.toastStatus(this);
    }

    public DropboxAPI<AndroidAuthSession> getDropbox() {
        return engine.getDropboxAPI(this);
    }

    public void createCooperTable() {
        ContentValues maleValues = new ContentValues();
        ContentValues femaleValues = new ContentValues();
        int[] ages = new int[]{14, 16, 20, 29, 39, 49, 50};
        int[][] male = new int[][]{{2700, 2400, 2200, 2100}, {2800, 2500, 2300, 2200}, {3000, 2700, 2500, 2300}, {2800, 2400, 2200, 1600}, {2700, 2300, 1900, 1500}, {2500, 2100, 1700, 1400}, {2400, 2000, 1600, 1300}};
        int[][] female = new int[][]{{2000, 1900, 1600, 1500}, {2100, 2000, 1700, 1600}, {2300, 2100, 1800, 1700}, {2700, 2200, 1800, 1500}, {2500, 2000, 1700, 1400}, {2300, 1900, 1500, 1200}, {2200, 1700, 1400, 1100}};
        for (int i = 0; i < ages.length; i++) {
            maleValues.put("age", ages[i]);
            maleValues.put("vgood", male[i][0]);
            maleValues.put("avgmax", male[i][1]);
            maleValues.put("avgmin", male[i][2]);
            maleValues.put("vbad", male[i][3]);
            femaleValues.put("age", ages[i]);
            femaleValues.put("vgood", female[i][0]);
            femaleValues.put("avgmax", female[i][1]);
            femaleValues.put("avgmin", female[i][2]);
            femaleValues.put("vbad", female[i][3]);
        }
        getContentResolver().insert(maleURI, maleValues);
        getContentResolver().insert(femaleURI, femaleValues);
    }

    public void checkDrop() {
        boolean checkDrop = prefs.getBoolean("checkDrop", false);
        if (!checkDrop) {
            engine.setMac("00:00:00:00:00:00");
            prefEdit.putString("mac_preference", engine.getMacAddress());
            prefEdit.apply();
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            prefEdit.putBoolean("prefDrop", true);
                            dropbox = getDropbox();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            prefEdit.putBoolean("prefDrop", false);
                            break;
                    }
                    prefEdit.putBoolean("checkDrop", true);
                    prefEdit.commit();
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to sync with Dropbox?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
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
        Intent intent = new Intent(this, Settings.class);
        intent.putExtra("engine", engine);
        startActivity(intent);

    }

    protected void onResume() {
        super.onResume();
        refreshStatus();
        boolean prefDrop = prefs.getBoolean("prefDrop", false);
        if (prefDrop) {
            if (dropbox == null) {
                dropbox = getDropbox();
            }
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
        Button rec = (Button) findViewById(R.id.recButton);
        Button cooper = (Button) findViewById(R.id.cooperButton);
        if (!engine.conStatus()) {
            rec.setEnabled(false);
            cooper.setEnabled(false);
        } else {
            rec.setEnabled(true);
            cooper.setEnabled(true);
        }
    }

    public void storePrefs(String token) {
        prefEdit.putString("token", token);
        prefEdit.apply();
    }

    public void refreshStatus() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        engine.setMac(prefs.getString("mac_preference", "FF:FF:FF:FF:FF:FF"));
        final TextView statusMAC = (TextView) findViewById(R.id.statusMAC);
        statusMAC.setText(engine.getMacAddress());
        try {
            engine.setSampleRate(Integer.valueOf(prefs.getString("list_preference", "100")));
        } catch (BITalinoException e) {
            e.printStackTrace();
        }
        final TextView statusSample = (TextView) findViewById(R.id.statusSample);
        statusSample.setText(String.valueOf(engine.getSampleRate()));
        final TextView statusConn = (TextView) findViewById(R.id.statusConn);
        if (checkBluetooth()) {
            engine.setConStatus(true);
            statusConn.setText("Available");
        } else {
            engine.setConStatus(false);
            statusConn.setText("Unavailable");
        }
    }

    public boolean checkBluetooth() {
        return true;
        /*bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) {
            if (bluetooth.isEnabled()) {
                String mydeviceaddress = bluetooth.getAddress();
                String mydevicename = bluetooth.getName();
                engine.setConStatus(true);
            } else {
                engine.setConStatus(false);
                return false;
            }
        } else {
            engine.setConStatus(false);
            return false;
        }
        BluetoothDevice bita = bluetooth.getRemoteDevice(engine.getMacAddress());
        if (bita != null && engine.conStatus()) {
            BluetoothSocket socket;
            try {
                socket = bita.createRfcommSocketToServiceRecord(engine.getUUID());
                socket.connect();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            try {
                engine.open(socket.getInputStream(), socket.getOutputStream());
            } catch (BITalinoException | IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;*/
    }

}

