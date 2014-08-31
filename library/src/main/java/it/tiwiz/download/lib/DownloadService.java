package it.tiwiz.download.lib;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

/**
 * This service will take care of responding to the commands
 * sent from the {@link android.app.Activity} based on the {@link Intent} Action specified.
 *
 * @see it.tiwiz.download.lib.DownloadActions#START_DOWNLOAD Start download IntentAction
 * @see it.tiwiz.download.lib.DownloadActions#CANCEL_DOWNLOAD Cancel download IntentAction
 */
public class DownloadService extends Service {

    private static int NOTIFICATION_ID = 1;
    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action.equals(DownloadActions.START_DOWNLOAD) && intent.hasExtra(DownloadActions.START_DOWNLOAD_EXTRA_FILENAME)) {
            String fileUrl = intent.getStringExtra(DownloadActions.START_DOWNLOAD_EXTRA_FILENAME);
            String filePath;

            if (intent.hasExtra(DownloadActions.START_DOWNLOAD_EXTRA_PATH)) {
                filePath = intent.getStringExtra(DownloadActions.START_DOWNLOAD_EXTRA_PATH);
            } else {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            Manager.startDownload(this, NOTIFICATION_ID, filePath, fileUrl);
            NOTIFICATION_ID++;
        } else if (action.equals(DownloadActions.CANCEL_DOWNLOAD) && intent.hasExtra(DownloadActions.CANCEL_DOWNLOAD_ID)) {
            Manager.cancelDownload(intent.getIntExtra(DownloadActions.CANCEL_DOWNLOAD_ID, -1));
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
