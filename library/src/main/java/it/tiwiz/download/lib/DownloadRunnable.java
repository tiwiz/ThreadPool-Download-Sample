package it.tiwiz.download.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Main {@link java.lang.Runnable} used to download a file.
 * Will abort in case of error.
 */
public class DownloadRunnable implements Runnable {
    protected String mPathOnSdCard;
    protected String mFileUrl;
    protected String mFileName;
    private DownloadTask mContainerTask;

    protected enum Result {
        SUCCESS,
        CANCELED,
        MALFORMED_URL,
        IO_EXCEPTION,
        FILE_NOT_FOUND
    }

    public DownloadRunnable(DownloadTask containerTask, String mPathOnSdCard, String mFileUrl) {
        this.mPathOnSdCard = mPathOnSdCard;
        this.mFileUrl = mFileUrl;
        mContainerTask = containerTask;
        mFileName = mFileUrl.substring(mFileUrl.lastIndexOf("/") + 1, mFileUrl.length());
    }

    @Override
    public void run() {
        Result result = Result.SUCCESS;
        mContainerTask.setCurrentThread(Thread.currentThread());
        File dir = new File(mPathOnSdCard);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        URL downloadUrl;
        try {
            downloadUrl = new URL(mFileUrl);
            URLConnection urlConnection = downloadUrl.openConnection();
            urlConnection.connect();
            final int fileSize = urlConnection.getContentLength();
            File downloadFile = new File(dir, mFileName);
            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(downloadFile);
            byte[] buffer = new byte[1024];
            int bufferLength, downloadedSize = 0;
            mContainerTask.publichDownloadStarted(mFileName);
            while (((bufferLength = inputStream.read(buffer)) > 0) && !Thread.interrupted()) {
                downloadedSize += bufferLength;
                outputStream.write(buffer, 0, bufferLength);
                float current = ((float) downloadedSize / (float) fileSize) * 100;
                mContainerTask.publishUpdate(current);
            }
            outputStream.close();
            inputStream.close();
            if(Thread.interrupted()) {
                result = Result.CANCELED;
                downloadFile.delete();
            }
        } catch (MalformedURLException e) {
            result = Result.MALFORMED_URL;
        } catch (FileNotFoundException e) {
            result = Result.FILE_NOT_FOUND;
        } catch (IOException e) {
            result = Result.IO_EXCEPTION;
        }
        mContainerTask.handleResult(result);
    }

}
