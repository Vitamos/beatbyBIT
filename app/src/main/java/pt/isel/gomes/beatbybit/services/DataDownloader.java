package pt.isel.gomes.beatbybit.services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;

import pt.isel.gomes.beatbybit.util.Frame;

/**
 * Created by Gomes on 20-05-2015.
 */
public class DataDownloader extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataDownloader(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int n = 0;
        ArrayList<Frame> frames = new ArrayList<>();
        long start = 0;
        while(true){
            long elapsed = chronometer.getBase() - start;
            if (elapsed >= 500){
                start+=500;
                frames.addAll(Arrays.asList(engine.open()));
                n+=500;

            }
        }
        return (Frame[]) frames.toArray();
    }
}
