package pt.isel.gomes.beatbybit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine implements Serializable {

    private final static String APP_KEY = "un624qhagsgq8wb";
    private final static String APP_SECRET = "wid188gkonsbj62";
    private final String[] sampleRates = {"10", "100", "1000"};
    private String sampleRate = sampleRates[1];
    private final BITalino bit;
    private String macAddress;

    public Engine() {
        bit = new BITalino();
    }

    public String connect() {
        return macAddress + " @ " + sampleRate + " Hz";
    }

    public boolean setMac(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$");
        Matcher m = p.matcher(mac);
        boolean result = m.find();
        if (result)
            macAddress = mac;
        return result;
    }


    public Frame[] open() {
        return bit.data(5);
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

    public void testCon() {
        System.out.println("Nao implementado");
    }

    public void setSampleRate(int choice) {
        this.sampleRate = sampleRates[choice];
    }


}
