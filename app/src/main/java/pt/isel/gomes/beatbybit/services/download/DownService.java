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
import pt.isel.gomes.beatbybit.util.comm.BITalinoException;
import pt.isel.gomes.beatbybit.util.comm.BITalinoFrame;


public class DownService extends IntentService {


    private SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
    private Engine engine;

    public DownService() {
        super("downService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        engine = Engine.getInstance();
        //Log.i("TESTSERVICE", "onCreate");
        ContentValues values = new ContentValues();
        //Log.i("TESTPROVIDER", String.valueOf(names.size()));
        BITalinoFrame[] a = new BITalinoFrame[0];
        try {
            a = engine.read(6);
        } catch (BITalinoException e) {
            e.printStackTrace();
        }
        for (BITalinoFrame f : a) {
            values.put("ecg", f.getAnalog(2));
            values.put("tags", engine.getTags());
            getContentResolver().insert(engine.getFileURI(), values);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
