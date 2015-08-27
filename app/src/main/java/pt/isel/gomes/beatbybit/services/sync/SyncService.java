package pt.isel.gomes.beatbybit.services.sync;

import android.app.IntentService;
import android.content.Intent;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.IOException;

import pt.isel.gomes.beatbybit.util.Engine;

public class SyncService extends IntentService {
    private Engine engine;
    private DropboxAPI<AndroidAuthSession> dropbox;

    public SyncService() {
        super("syncService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        engine = Engine.getInstance();
        dropbox = engine.getDropboxAPI(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            engine.testCloud(dropbox);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }
    }
}
