package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;


public class Rec extends Activity {
    private Chronometer chronometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
    }

    public void startClock(View v){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }
    public void stopClock(View v){
        chronometer.stop();
    }
}
