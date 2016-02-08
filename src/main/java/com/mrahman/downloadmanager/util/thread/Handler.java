package com.mrahman.downloadmanager.util.thread;

/**
 * @author mizanur.rahman
 *
 */
public class Handler {

    public void handleMessage(Message msg) {}

    public void sendMessage(Message msg) {
        handleMessage(msg);
    }
}
