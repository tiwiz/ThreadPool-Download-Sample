package it.tiwiz.download.lib;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class implements a custom {@link java.util.concurrent.ThreadPoolExecutor}
 * to manage multiple downloads at a time
 *
 * @see #startDownload(android.content.Context, int, String, String) startDownload()
 * @see #cancelDownload(int) cancelDownload()
 * methods for a quick overview of the functionalities.
 */
public class Manager {
    private static Manager sInstance;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int MAXIMUM_CORES_POOL = 8;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int KEEP_ALIVE_TIME = 10;
    private final BlockingQueue<Runnable> mDownloadQueue;
    private final SparseArray<DownloadTask> mTaskArray;
    private final Queue<DownloadTask> mTaskQueue;
    private final ThreadPoolExecutor mDownloadPool;
    Handler mHandler;

    public enum Status {
        STARTED,
        UPDATED,
        COMPLETED,
        ERROR,
        CANCELED
    }

    /**
     * Grants the <b>Singleton</b> paradigm with a private constructor
     */
    private Manager() {
        mDownloadQueue = new LinkedBlockingQueue<Runnable>();
        mTaskQueue = new LinkedBlockingQueue<DownloadTask>();
        mTaskArray = new SparseArray<DownloadTask>();
        mDownloadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, MAXIMUM_CORES_POOL, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, mDownloadQueue);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
    }

    static {
       sInstance = new Manager();
    }

    public static Manager getInstance() {
        return sInstance;
    }

    /**
     * This <code>static</code> method will initiate a {@link it.tiwiz.download.lib.DownloadTask} for the
     * selected file.
     */
    public static DownloadTask startDownload(Context context, int notificationId, String pathOnSdCard, String fileUrl) {
        DownloadTask downloadTask = sInstance.mTaskQueue.poll();
        if (downloadTask == null) {
            downloadTask = new DownloadTask();
        }
        downloadTask.initDownloadTask(context, sInstance, notificationId, pathOnSdCard, fileUrl);
        sInstance.mDownloadPool.execute(downloadTask.getDownloadRunnable());
        sInstance.mTaskArray.put(notificationId, downloadTask);
        return downloadTask;
    }

    /**
     * This method will cancel a previously started download
     * @param id of the notification (and download) to be stopped
     */
    public static void cancelDownload(int id) {

        if (id != -1) {
            DownloadTask downloadTask = sInstance.mTaskArray.get(id, null);

            if (downloadTask != null) {
                synchronized (sInstance) {
                    Thread currentThread = downloadTask.getCurrentThread();
                    if (currentThread != null) {
                        currentThread.interrupt();
                    }
                }
                sInstance.mDownloadPool.remove(downloadTask.getDownloadRunnable());
                sInstance.mTaskArray.remove(id);
            }
        }
    }

    public void handleStatus(Context context, Status status, int id, Object additionalData) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;

        switch(status) {
            case STARTED:
                notification = DownloadNotificationBuilder.downloadStarted(context, (String) additionalData, id);
                break;
            case UPDATED:
                notification = DownloadNotificationBuilder.downloadUpdated(context, (Float) additionalData, id);
                break;
            case ERROR:
                notification = DownloadNotificationBuilder.downloadError(context, (String) additionalData);
                mTaskArray.remove(id);
                break;
            case COMPLETED:
                notification = DownloadNotificationBuilder.downloadCompleted(context, (String) additionalData);
                mTaskArray.remove(id);
                break;
            case CANCELED:
                notification = DownloadNotificationBuilder.downloadCanceled(context);
                break;
            default:
                notification = null;
                break;
        }

        if (notification != null) {
            notificationManager.notify(id, notification);
        }
    }

}
