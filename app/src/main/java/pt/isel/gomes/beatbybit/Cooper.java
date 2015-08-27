package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.Toast;

import pt.isel.gomes.beatbybit.util.Engine;


public class Cooper extends Activity {
    private Chronometer chronometer;
    private Engine engine;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooper);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        engine = (Engine) getIntent().getSerializableExtra("engine");
    }

    public void startClock(View v) {
        running = true;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, engine.connect(), duration);
        toast.show();

    }

    public void stopClock(View v) {
        if (running) {
            running = false;
            chronometer.stop();
            final NumberPicker agePicker = new NumberPicker(this);
            agePicker.setMaxValue(150);
            agePicker.setMinValue(13);
            final AlertDialog.Builder sexDialog = new AlertDialog.Builder(this);
            sexDialog.setTitle("Gender?");

            sexDialog.setPositiveButton("Male",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

            sexDialog.setNeutralButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

            sexDialog.setNegativeButton("Female",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });


            AlertDialog.Builder ageAlert = new AlertDialog.Builder(this);
            ageAlert.setView(agePicker);
            ageAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int age = agePicker.getValue();
                    sexDialog.show();
                }
            });

            ageAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Cancel.
                }
            });


            ageAlert.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopClock(getCurrentFocus());
    }
}