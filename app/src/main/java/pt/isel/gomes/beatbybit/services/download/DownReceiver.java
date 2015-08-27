package pt.isel.gomes.beatbybit.services.download;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import pt.isel.gomes.beatbybit.util.Engine;

public class DownReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Engine engine = (Engine) intent.getSerializableExtra("engine");
        Intent downIntent = new Intent(context, DownService.class);
        downIntent.putExtra("engine", engine);
        PendingIntent pendingAlarmIntent = PendingIntent.getService(context, 0, downIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Log.i("TESTSERVICE", "onReceive");
        if (intent.getAction().equals("pt.isel.gomes.beatbybit.ACTION.start")) {
            //Log.i("TESTSERVICE", "startService");
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    1000,
                    pendingAlarmIntent);
        } else if (intent.getAction().equals("pt.isel.gomes.beatbybit.ACTION.stop")) {
            context.stopService(downIntent);
            alarmManager.cancel(pendingAlarmIntent);
            //Log.i("TESTSERVICE", "stopService");
        }
    }
}
