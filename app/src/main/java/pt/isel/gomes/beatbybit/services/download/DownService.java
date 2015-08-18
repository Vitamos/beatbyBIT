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
import pt.isel.gomes.beatbybit.util.comm.BITalinoException;
import pt.isel.gomes.beatbybit.util.comm.BITalinoFrame;


public class DownService extends IntentService {

    private final String PROVIDER_NAME = "com.example.provider.DownProvider";
    private final String URL = "content://" + PROVIDER_NAME + "/data";
    private final Uri URI = Uri.parse(URL);
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
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
        BITalinoFrame[] a = new BITalinoFrame[0];
        try {
            a = engine.read(6);
        } catch (BITalinoException e) {
            e.printStackTrace();
        }
        for (BITalinoFrame f : a) {
            values.put("c1", f.getAnalog(0));
            values.put("c2", f.getAnalog(1));
            values.put("c3", f.getAnalog(2));
            values.put("c4", f.getAnalog(3));
            values.put("c5", f.getAnalog(4));
            values.put("c6", f.getAnalog(5));
            values.put("date", format.format(c.getTime()));
        }


        getContentResolver().insert(URI, values);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TESTSERVICE", "started");
        //Toast.makeText(getApplicationContext(),"teste",Toast.LENGTH_SHORT);
    }

}
