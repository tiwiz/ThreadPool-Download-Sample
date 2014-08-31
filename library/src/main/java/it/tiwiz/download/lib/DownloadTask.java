package it.tiwiz.download.lib;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * This task contains all the data needed to download a file,
 * notification id included.
 */
public class DownloadTask {

    private int mId;
    private Runnable mDownloadRunnable;
    private Thread mCurrentThread;
    private static Manager sManager;
    private WeakReference<Context> mContext;
    private String mFilename;
    private String mFileUrl;

    public DownloadTask() {
        sManager = Manager.getInstance();
    }

    /**
     * This method will initializate a download creatig a {@link java.lang.ref.WeakReference} to
     * the {@link android.content.Context} in order to avoid leakage
     */
    public void initDownloadTask(Context context, Manager manager, int id, String pathOnSdCard, String fileUrl) {
        mDownloadRunnable = new DownloadRunnable(this, pathOnSdCard, fileUrl);
        sManager = manager;
        mId = id;
        mFileUrl = fileUrl;
        mContext = new WeakReference<Context>(context);
    }

    /**
     * This method will store in a <i>thread-safe</i> way the
     * current thread of download for interrupting it.
     *
     * @see #getCurrentThread() for getter counterpart
     */
    public void setCurrentThread(Thread currentThread) {
        synchronized (sManager) {
            mCurrentThread = currentThread;
        }
    }

    public Thread getCurrentThread() {
        synchronized (sManager) {
            return mCurrentThread;
        }
    }

    public Runnable getDownloadRunnable() {
        return mDownloadRunnable;
    }

    public void publichDownloadStarted(String filename) {
        sManager.handleStatus(mContext.get(), Manager.Status.STARTED, mId, filename);
        mFilename = filename;
    }

    public void publishUpdate(float percentage) {
        sManager.handleStatus(mContext.get(), Manager.Status.UPDATED, mId, percentage);
    }

    public void handleResult(DownloadRunnable.Result result) {
        if (result == DownloadRunnable.Result.SUCCESS) {
            sManager.handleStatus(mContext.get(), Manager.Status.COMPLETED, mId, mFilename);
        } else if (result == DownloadRunnable.Result.CANCELED) {
            sManager.handleStatus(mContext.get(), Manager.Status.CANCELED, mId, null);
        } else {
            sManager.handleStatus(mContext.get(), Manager.Status.ERROR, mId, mFileUrl);
        }
    }

}
