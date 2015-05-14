package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import pt.isel.gomes.beatbybit.util.Engine;


public class Rec extends Activity {
    private Chronometer chronometer;
    private Engine engine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        engine = (Engine) getIntent().getSerializableExtra("engine");
    }

    public void startClock(View v){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, engine.connect(), duration);
        toast.show();

    }
    public void stopClock(View v){
        chronometer.stop();
    }


}
