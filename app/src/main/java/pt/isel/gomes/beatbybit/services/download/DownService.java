package pt.isel.gomes.beatbybit.services.download;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

import pt.isel.gomes.beatbybit.util.Engine;
import pt.isel.gomes.beatbybit.util.Frame;


public class DownService extends IntentService {
    static final String c1 = "c1";
    static final String c2 = "c2";
    static final String c3 = "c3";
    static final String c4 = "c4";
    static final String c5 = "c5";
    static final String c6 = "c6";
    static final String date = "date";
    private final String PROVIDER_NAME = "com.example.provider.DownProvider";
    private final String URL = "content://" + PROVIDER_NAME + "/data";
    private final Uri URI = Uri.parse(URL);
    Calendar c = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
    private Engine engine;
    public DownService() {
        super("downService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TESTSERVICE", "onCreate");
        engine = new Engine();
        ContentValues values = new ContentValues();
        Set names = values.keySet();
        Log.i("TESTPROVIDER", String.valueOf(names.size()));
        Frame[] a = engine.open();
        for (Frame f : a) {
            int[] aux = f.analog;
            values.put(c1, aux[0]);
            values.put(c2, aux[1]);
            values.put(c3, aux[2]);
            values.put(c4, aux[3]);
            values.put(c5, aux[4]);
            values.put(c6, aux[5]);
            values.put(date, format.format(c.getTime()));
        }


        getContentResolver().insert(URI, values);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TESTSERVICE", "started");
        //Toast.makeText(getApplicationContext(),"teste",Toast.LENGTH_SHORT);
    }

}
