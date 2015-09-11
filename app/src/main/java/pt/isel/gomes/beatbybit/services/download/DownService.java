package pt.isel.gomes.beatbybit.services.download;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Locale;

import pt.isel.gomes.beatbybit.util.Engine;
import pt.isel.gomes.beatbybit.util.comm.BITalinoException;
import pt.isel.gomes.beatbybit.util.comm.BITalinoFrame;


public class DownService extends IntentService {


    private SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.ROOT);
    private Engine engine;

    public DownService() {
        super("downService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        engine = Engine.getInstance();
        if (engine.conStatus()) {
            ContentValues values = new ContentValues();
            BITalinoFrame[] a = new BITalinoFrame[0];
            try {
                a = engine.read(engine.getSampleRate());
            } catch (BITalinoException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                return 0;
            }
            for (BITalinoFrame f : a) {
                values.put("ecg", f.getAnalog(2));
                values.put("tags", engine.getTags());
                getContentResolver().insert(engine.getFileURI(), values);
            }
        }
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
