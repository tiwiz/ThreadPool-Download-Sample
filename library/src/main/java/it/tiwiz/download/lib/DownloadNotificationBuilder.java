package it.tiwiz.download.lib;

import android.app.Notification;
import android.content.Context;

/**
 * This class is a collection of static factory methods to create sample {@link android.app.Notification}
 */
public class DownloadNotificationBuilder {

    public static Notification downloadStarted(Context context, String filename, int id) {
        return new Notification.Builder(context)
                .setContentTitle("Downloading image: " + filename)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Tap to cancel")
                .setProgress(100, 0, true)
                .setOngoing(true)
                .setContentIntent(DownloadActions.getCancelDownloadPendingIntent(context, id))
                .build();
    }

    public static Notification downloadUpdated(Context context, float percentage, int id) {
        return new Notification.Builder(context)
                .setContentTitle("Downloading image")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Tap to cancel")
                .setProgress(100, Math.round(percentage), false)
                .setOngoing(true)
                .setContentIntent(DownloadActions.getCancelDownloadPendingIntent(context, id))
                .build();
    }

    public static Notification downloadCompleted(Context context, String filename) {
        return new Notification.Builder(context)
                .setContentTitle("Image downloaded")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Tap to open")
                .setContentIntent(DownloadActions.getOpenDownloadedFileIntent(context, filename))
                .build();
    }

    public static Notification downloadError(Context context, String fileUrl) {
        return new Notification.Builder(context)
                .setContentTitle("Image not downloaded")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Tap to try again")
                .setContentIntent(DownloadActions.getRetryDownloadPendingIntent(context, fileUrl))
                .build();
    }

    public static Notification downloadCanceled(Context context) {
        return new Notification.Builder(context)
                .setContentTitle("Image download has been canceled")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Swipe away to dismiss")
                .build();
    }
}
