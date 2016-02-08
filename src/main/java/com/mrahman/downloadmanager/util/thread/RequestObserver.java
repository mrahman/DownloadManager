/**
 * 
 */
package com.mrahman.downloadmanager.util.thread;

/**
 * @author mizanur.rahman
 *
 */
public interface RequestObserver {

    public void observe(DownloadRunnable runnable, String type);

}
