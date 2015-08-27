package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.net.URI;
import java.net.URL;

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
        engine = Engine.getInstance();
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
            final int[] agesex = new int[2];
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
                            agesex[1] = 0;
                            int age = agesex[0];
                            if (age < 15)
                                age = 14;
                            else if (age < 17)
                                age = 16;
                            else if (age < 21)
                                age = 20;
                            else if (age < 30)
                                age = 29;
                            else if (age < 40)
                                age = 39;
                            else if (age < 50)
                                age = 49;
                            else
                                age = 50;
                            // Log.i("AGE : ", String.valueOf(agesex[0]));
                            Log.i("CALCULATED AGE: ", String.valueOf(age));
                            // Log.i("SEX : ", "Male");
                            Cursor cursor = getContentResolver().query(engine.getMaleURI(), null, "age = ?", new String[]{String.valueOf(age)}, null);
                            cursor.moveToFirst();
                            Log.i("CURSOR: ", String.valueOf(cursor.getCount()));

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
                            agesex[1] = 1;
                            Log.i("AGE : ", String.valueOf(agesex[0]));
                            Log.i("SEX : ", "Female");
                        }
                    });


            AlertDialog.Builder ageAlert = new AlertDialog.Builder(this);
            ageAlert.setView(agePicker);
            ageAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    agesex[0] = agePicker.getValue();
                    sexDialog.show();
                }
            });

            ageAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
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