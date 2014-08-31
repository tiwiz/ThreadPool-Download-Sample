package it.tiwiz.download.lib;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tiwiz on 30/08/14.
 */
public class DownloadActions {

    private static final String PREFIX = "it.tiwiz.download.lib";
    public static final String CANCEL_DOWNLOAD = PREFIX + ".CANCEL_DOWNLOAD";
    public static final String CANCEL_DOWNLOAD_ID = CANCEL_DOWNLOAD + ".ID";
    public static final String OPEN_DOWNLOADED_FILE = PREFIX + ".OPEN_DOWNLOADED";
    public static final String OPEN_DOWNLOADED_FILE_NAME = OPEN_DOWNLOADED_FILE + ".NAME";
    public static final String START_DOWNLOAD = PREFIX + ".START_DOWNLOAD";
    public static final String START_DOWNLOAD_EXTRA_FILENAME = START_DOWNLOAD + ".EXTRA_FILENAME";
    public static final String START_DOWNLOAD_EXTRA_PATH = START_DOWNLOAD + ".EXTRA_PATH";

    public static PendingIntent getCancelDownloadPendingIntent(Context context, int id) {
        final Intent serviceIntent = new Intent(context, DownloadService.class);
        serviceIntent.setAction(DownloadActions.CANCEL_DOWNLOAD);
        serviceIntent.putExtra(CANCEL_DOWNLOAD_ID, id);
        return PendingIntent.getService(context, 0, serviceIntent, 0);
    }

    public static PendingIntent getRetryDownloadPendingIntent(Context context, String fileUrl) {
        return PendingIntent.getService(context, 0, getDownloadIntent(context, fileUrl), 0);
    }

    public static Intent getDownloadIntent(Context context, String fileUrl) {
        final Intent serviceIntent = new Intent(context, DownloadService.class);
        serviceIntent.setAction(DownloadActions.START_DOWNLOAD);
        serviceIntent.putExtra(DownloadActions.START_DOWNLOAD_EXTRA_FILENAME, fileUrl);
        return serviceIntent;
    }

    public static PendingIntent getOpenDownloadedFileIntent(Context context, String fileName) {
        final Intent broadcastIntent = new Intent(OPEN_DOWNLOADED_FILE);
        broadcastIntent.putExtra(OPEN_DOWNLOADED_FILE_NAME, fileName);
        return PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }


}
