package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pt.isel.gomes.beatbybit.services.download.DownReceiver;
import pt.isel.gomes.beatbybit.services.sync.SyncService;
import pt.isel.gomes.beatbybit.util.Engine;


public class Cooper extends Activity {
    private Chronometer chronometer;
    private Engine engine;
    private boolean running;
    private LocationManager locationManager;
    private float dist;
    private Location locAux = null;
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat date = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
    private SimpleDateFormat time = new SimpleDateFormat("hhmm", Locale.ROOT);
    private String finalResult;
    private int age;
    private String sex;

    private void gpsDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooper);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        engine = Engine.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                if (elapsedMillis >= 720000) {
                    stopClock(getCurrentFocus());
                }
            }
        });
    }

    public void startClock(View v) {
        dist = 0;
        locAux = null;
        running = true;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 1, new MyLocationListener());
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        Intent intent = new Intent(this, DownReceiver.class);
        intent.setAction("pt.isel.gomes.beatbybit.ACTION.start");
        sendBroadcast(intent);
    }

    public void stopClock(View v) {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        float totalDist = dist;
        if (running) {
            running = false;
            chronometer.stop();
            Intent intent = new Intent(this, DownReceiver.class);
            intent.setAction("pt.isel.gomes.beatbybit.ACTION.stop");
            sendBroadcast(intent);
            if (elapsedMillis >= 10000) {
                dialogs(totalDist);
            } else {
                Toast.makeText(getApplicationContext(), "That was less than 12 minutes!", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void end(){
        Cursor cursor = getContentResolver().query(engine.getFileURI(), null, null, null, null);
        String[] data = engine.createFile(cursor);
        engine.writeToFile("cooper_" + date.format(c.getTime()) + "_" + time.format(c.getTime()) + "_" + engine.getSampleRate() + "_" + finalResult + "_" + age + "_" + sex + ".txt", data);
        getContentResolver().delete(engine.getFileURI(), null, null);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean prefDrop = prefs.getBoolean("prefDrop", false);
        if (prefDrop) {
            Intent intent = new Intent(this, SyncService.class);
            startService(intent);
        }
    }
    public void dialogs(final float d) {
        final int[] ageList = new int[2];

        final AlertDialog.Builder sexDialog = new AlertDialog.Builder(this);
        sexDialog.setTitle("Your gender?");
        sexDialog.setPositiveButton("Male",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        age = ageList[1];
                        finalResult = getResult(true, age, d);
                        sex = "M";
                        Toast.makeText(getApplicationContext(), finalResult, Toast.LENGTH_LONG).show();
                        end();
                    }
                });

        sexDialog.setNegativeButton("Female",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        age = ageList[1];
                        finalResult = getResult(false, age, d);
                        sex = "F";
                        Toast.makeText(getApplicationContext(), finalResult, Toast.LENGTH_LONG).show();
                        end();
                    }
                });


        final NumberPicker agePicker = new NumberPicker(this);
        agePicker.setMaxValue(99);
        agePicker.setMinValue(13);

        AlertDialog.Builder ageAlert = new AlertDialog.Builder(this);
        ageAlert.setTitle("Your age?");
        ageAlert.setView(agePicker);
        ageAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ageList[0] = agePicker.getValue();
                ageList[1] = ageList[0];
                int[] ages = new int[]{14, 16, 20, 29, 39, 49, 50};
                for (int a : ages) {
                    if (ageList[1] <= a) {
                        ageList[1] = a;
                        break;
                    }
                }
                if (ageList[1] > 50) {
                    ageList[1] = 50;
                }
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

    public String getResult(boolean male, int age, float d) {
        Cursor cursor;
        if (male)
            cursor = getContentResolver().query(engine.getMaleURI(), null, "age = ?", new String[]{String.valueOf(age)}, null);
        else
            cursor = getContentResolver().query(engine.getFemaleURI(), null, "age = ?", new String[]{String.valueOf(age)}, null);
        if (cursor.moveToFirst()) {
            int vgood = cursor.getInt(1);
            int avgmax = cursor.getInt(2);
            int avgmin = cursor.getInt(3);
            int vbad = cursor.getInt(4);
            String result;
            if (d > vgood)
                result = "Very good!";
            else if (d > avgmax)
                result = "Good!";
            else if (d > avgmin)
                result = "Average!";
            else if (d > vbad)
                result = "Bad!";
            else
                result = "Very Bad!";
            return result;
        }
        return null;
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            if (locAux != null) {
                dist += location.distanceTo(locAux);
            }
            locAux = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            gpsDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (!running)
            super.onBackPressed();
    }
}