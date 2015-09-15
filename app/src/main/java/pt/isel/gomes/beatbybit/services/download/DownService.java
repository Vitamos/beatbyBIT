package pt.isel.gomes.beatbybit.services.download;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Locale;

import pt.isel.gomes.beatbybit.util.Engine;
import pt.isel.gomes.beatbybit.util.SensorDataConverter;
import pt.isel.gomes.beatbybit.util.comm.BITalinoException;
import pt.isel.gomes.beatbybit.util.comm.BITalinoFrame;


public class DownService extends IntentService {


    private SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
    private Engine engine;
    private SensorDataConverter converter = new SensorDataConverter();

    public DownService() {
        super("downService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread("DownService") {
            public void run() {
                engine = Engine.getInstance();
                if (engine.conStatus()) {
                    ContentValues values = new ContentValues();
                    BITalinoFrame[] a = new BITalinoFrame[0];
                    try {
                        a = engine.read(engine.getSampleRate());
                    } catch (BITalinoException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {

                    }
                    for (BITalinoFrame f : a) {
                        double value = converter.scaleECG(2, f.getAnalog(2));
                        values.put("ecg", String.valueOf(value));
                        values.put("tags", engine.getTags());
                        getContentResolver().insert(engine.getFileURI(), values);
                    }
                }

            }
        };
        thread.start();
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
