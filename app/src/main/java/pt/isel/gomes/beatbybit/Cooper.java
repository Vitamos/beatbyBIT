package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
    private LocationManager locationManager;
    private float dist;
    private Location locAux = null;

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude());

            if (locAux != null) {
                dist += location.distanceTo(locAux);
            }
            locAux = location;
            Toast.makeText(Cooper.this, String.valueOf(dist), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

     /*   public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(Cooper.this, "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(Cooper.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(Cooper.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }*/

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooper);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        engine = Engine.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void startClock(View v) {
        dist = 0;
        locAux = null;
        running = true;
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3,
                1,
                new MyLocationListener()
        );
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
            agePicker.setMaxValue(99);
            agePicker.setMinValue(13);
            final AlertDialog.Builder sexDialog = new AlertDialog.Builder(this);
            sexDialog.setTitle("Gender?");
            sexDialog.setPositiveButton("Male",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            agesex[1] = 0;
                            int age = agesex[0];
                            int[] ages = new int[]{14, 16, 20, 29, 39, 49, 50};
                            for (int a : ages) {
                                if (age <= a) {
                                    age = a;
                                    break;
                                }
                            }
                            if (age > 50) {
                                age = 50;
                            }
                            Cursor cursor = getContentResolver().query(engine.getMaleURI(), null, "age = ?", new String[]{String.valueOf(age)}, null);
                            if (cursor.moveToFirst()) {
                                int vgood = cursor.getInt(1);
                                int avgmax = cursor.getInt(2);
                                int avgmin = cursor.getInt(3);
                                int vbad = cursor.getInt(4);
                                String result;
                                if (dist > vgood)
                                    result = "Very good!";
                                else if (dist > avgmax)
                                    result = "Good!";
                                else if (dist > avgmin)
                                    result = "Average!";
                                else if (dist > vbad)
                                    result = "Bad!";
                                else
                                    result = "Very Bad!";
                                Log.i("RESULT: ", result);
                            }
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
            ageAlert.setTitle("Age?");
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