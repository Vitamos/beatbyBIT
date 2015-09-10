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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pt.isel.gomes.beatbybit.services.download.DownReceiver;
import pt.isel.gomes.beatbybit.services.sync.SyncService;
import pt.isel.gomes.beatbybit.util.Engine;
import pt.isel.gomes.beatbybit.util.comm.BITalinoException;


public class Rec extends Activity {

    private Engine engine = Engine.getInstance();
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat date = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
    private SimpleDateFormat time = new SimpleDateFormat("hhmm", Locale.ROOT);
    private Chronometer chronometer;

    private boolean running;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        engine = Engine.getInstance();
        chronometer = (Chronometer) findViewById(R.id.chronometer);

    }

    public void startClock(View v) {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        running = true;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        Intent intent = new Intent(this, DownReceiver.class);
        intent.setAction("pt.isel.gomes.beatbybit.ACTION.start");
        sendBroadcast(intent);
    }

    public void stopClock(View v) {
        if (running) {
            running = false;
            chronometer.stop();
            Intent intent = new Intent(this, DownReceiver.class);
            intent.setAction("pt.isel.gomes.beatbybit.ACTION.stop");
            sendBroadcast(intent);
            Cursor cursor = getContentResolver().query(engine.getFileURI(), null, null, null, null);
            String[] data = engine.createFile(cursor);
            engine.writeToFile("rec_" + date.format(c.getTime()) + "_" + time.format(c.getTime()) + "_" + engine.getSampleRate() + ".txt", data);
            getContentResolver().delete(engine.getFileURI(), null, null);
            //SO ACONTECE SE TIVER DROPBOX ASSOCIADA
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean prefDrop = prefs.getBoolean("prefDrop", false);
            if (prefDrop) {
                intent = new Intent(this, SyncService.class);
                startService(intent);
            }
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!running)
            super.onBackPressed();
    }

}