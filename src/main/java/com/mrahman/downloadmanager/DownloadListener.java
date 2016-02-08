package com.mrahman.downloadmanager;

import com.mrahman.downloadmanager.data.ProgressEntity;
import com.mrahman.downloadmanager.data.ResultEntity;


public interface DownloadListener {
    /**
     * @param result function for handling result
     */
    public void onResult(ResultEntity result);

    /**
     * @param error function for handling Error
     */
    public void onError(String error);

    /**
     * @param progress of type ProgressEntity function for handling progress
     */
    public void onProgress(ProgressEntity progress);
}
