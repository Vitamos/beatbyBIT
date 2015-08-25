package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Chronometer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pt.isel.gomes.beatbybit.services.download.DownReceiver;
import pt.isel.gomes.beatbybit.services.sync.SyncService;
import pt.isel.gomes.beatbybit.util.Engine;


public class Rec extends Activity {

    private static final String PROVIDER_NAME = "com.example.provider.DownProvider";
    private static final String URL = "content://" + PROVIDER_NAME + "/data";
    private Uri URI;
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat date = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
    private SimpleDateFormat time = new SimpleDateFormat("hhmm", Locale.ROOT);
    private Chronometer chronometer;
    private Engine engine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        engine = (Engine) getIntent().getSerializableExtra("engine");
        URI = Uri.parse(URL);
        chronometer = (Chronometer) findViewById(R.id.chronometer);

    }

    public void startClock(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        Intent intent = new Intent(this, DownReceiver.class);
        intent.putExtra("engine", engine);
        intent.setAction("pt.isel.gomes.beatbybit.ACTION.start");
        sendBroadcast(intent);
    }

    public void stopClock(View v) {
        chronometer.stop();
        Intent intent = new Intent(this, DownReceiver.class);
        intent.setAction("pt.isel.gomes.beatbybit.ACTION.stop");
        sendBroadcast(intent);
        Cursor cursor = getContentResolver().query(URI, null, null, null, null);
        String[] data = engine.createFile(cursor);
        engine.writeToFile("rec_" + date.format(c.getTime()) + "_" + time.format(c.getTime()) + "_" + engine.getSampleRate() + ".txt", data);
        getContentResolver().delete(URI, null, null);
        //SO ACONTECE SE TIVER DROPBOX ASSOCIADA
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean prefDrop = prefs.getBoolean("prefDrop", false);
        if (prefDrop) {
            intent = new Intent(this, SyncService.class);
            startService(intent);
        }
    }

    public void tag1() {
        engine.toggleTag(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (chronometer.isActivated())
            stopClock(getCurrentFocus());
    }

}