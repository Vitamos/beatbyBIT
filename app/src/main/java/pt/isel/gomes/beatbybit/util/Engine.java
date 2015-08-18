package pt.isel.gomes.beatbybit.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.isel.gomes.beatbybit.util.comm.BITalinoDevice;
import pt.isel.gomes.beatbybit.util.comm.BITalinoException;
import pt.isel.gomes.beatbybit.util.comm.BITalinoFrame;

public class Engine implements Serializable {

    private final static String APP_KEY = "un624qhagsgq8wb";
    private final static String APP_SECRET = "wid188gkonsbj62";
    private int sampleRate = 100;
    private BITalinoDevice bit;
    private String macAddress;
    private BluetoothAdapter bluetooth;
    private String status;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    public Engine() {
        try {
            bit = new BITalinoDevice(sampleRate, new int[]{0, 1, 2, 3, 4, 5});
        } catch (BITalinoException e) {
            e.printStackTrace();
        }
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) {
            if (bluetooth.isEnabled()) {
                String mydeviceaddress = bluetooth.getAddress();
                String mydevicename = bluetooth.getName();
                status = mydevicename + " : " + mydeviceaddress;
            } else {
                status = "Bluetooth is not Enabled.";
            }
        } else {
            status = "NULL";
        }
    }

    public String connect() {
        return macAddress + " @ " + sampleRate + " Hz";
    }

    public UUID getUUID() {
        return this.MY_UUID;
    }

    public boolean setMac(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$");
        Matcher m = p.matcher(mac);
        boolean result = m.find();
        if (result)
            macAddress = mac;
        return result;
    }


    public BITalinoFrame[] read(int samples) throws BITalinoException {
        //return bit.read(samples);
        BITalinoFrame[] b = new BITalinoFrame[samples];
        for (int i = 0; i < samples; i++) {
            b[i] = new BITalinoFrame();
            b[i].setAnalog(0, 1);
            b[i].setAnalog(1, 1);
            b[i].setAnalog(2, 1);
            b[i].setAnalog(3, 1);
            b[i].setAnalog(4, 1);
            b[i].setAnalog(5, 1);

        }
        return b;
    }

    public void open(InputStream is, OutputStream os) throws BITalinoException {
        bit.open(is, os);
    }

    public DropboxAPI<AndroidAuthSession> getDropboxAPI(Context c) {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        DropboxAPI<AndroidAuthSession> mDBApi = new DropboxAPI<>(session);
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(c);
        String token = sharedpreferences.getString("token", null);
        if (token == null) {
            mDBApi.getSession().startOAuth2Authentication(c);
            return mDBApi;
        }
        mDBApi.getSession().setOAuth2AccessToken(token);
        return mDBApi;
    }

    public void close() {
        System.out.println("Nao implementado");
    }

    public String[] createFile(Cursor cursor) {
        String[] values = new String[cursor.getCount()];
        Log.i("TESTPROVIDER", String.valueOf(cursor.getCount()));
        Log.i("TESTPROVIDER", String.valueOf(cursor.getColumnCount()));
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String line = "";
            line += cursor.getString(0);
            for (int j = 1; j < cursor.getColumnCount(); j++) {
                line += "," + cursor.getString(j);
            }
            values[i] = line;
        }
        cursor.close();
        return values;
    }

    public void writeToFile(String file, String[] data) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root, "beat");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File(dir + "/" + file);
        try {
            FileOutputStream out = new FileOutputStream(f);
            for (String s : data) {
                s += "\n";
                out.write(s.getBytes());
            }
            out.close();

        } catch (Exception e) {
        }
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String[] getFiles() {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root, "beat");
        return dir.list();
    }

    public void testCloud(DropboxAPI<AndroidAuthSession> dropbox) throws IOException, DropboxException {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root, "beat");
        String[] values = getFiles();
        for (String s : values) {
            File f = new File(dir, s);
            FileInputStream inputStream = new FileInputStream(f);
            dropbox.putFileOverwrite(s, inputStream, f.length(), null);
        }
    }

    public boolean testCon() throws BITalinoException {
        return bit.version() != null;
    }

    public void setSampleRate(int choice) throws BITalinoException {
        this.sampleRate = choice;
        this.bit = new BITalinoDevice(this.sampleRate, new int[]{0, 1, 2, 3, 4, 5});
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public String getStatus() {
        return this.status;
    }

    public void toastStatus(Context c) {
        Toast.makeText(c, this.status, Toast.LENGTH_LONG);
    }

    public BluetoothDevice startBluetooth() {
        return bluetooth.getRemoteDevice(macAddress);

    }
}
