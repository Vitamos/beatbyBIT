package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;

import pt.isel.gomes.beatbybit.services.download.DownReceiver;
import pt.isel.gomes.beatbybit.util.Engine;


public class Rec extends Activity {
    private Chronometer chronometer;
    private Engine engine;
    private boolean recording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        engine = (Engine) getIntent().getSerializableExtra("engine");
    }

    public void startClock(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
      /*  Toast toast = Toast.makeText(context, engine.connect(), duration);
        toast.show();*/

        Intent intent = new Intent(this,DownReceiver.class);
        intent.setAction("pt.isel.gomes.beatbybit.ACTION.start");

        sendBroadcast(intent);
    }

    public void stopClock(View v) {
        chronometer.stop();
        Intent intent = new Intent(this,DownReceiver.class);
        intent.setAction("pt.isel.gomes.beatbybit.ACTION.stop");
        sendBroadcast(intent);
    }
}