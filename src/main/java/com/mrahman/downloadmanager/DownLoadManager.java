/**
 * 
 */
package com.mrahman.downloadmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HTTP;

import com.mrahman.downloadmanager.util.param.ParamsUtils;
import com.mrahman.downloadmanager.util.param.RequestMethods;
import com.mrahman.downloadmanager.util.param.ResultFormats;
import com.mrahman.downloadmanager.util.thread.DownloadRunnable;
import com.mrahman.downloadmanager.util.thread.Handler;
import com.mrahman.downloadmanager.util.thread.Message;
import com.mrahman.downloadmanager.data.ParamsEntity;
import com.mrahman.downloadmanager.data.ResultEntity;

/**
 * @author mizanur.rahman
 *
 */
public class DownLoadManager {

    public static final int PROGRESS = 0;
    public static final int COMPLETE = 1;

    private static DownLoadManager _instance;

    private HashMap<String, byte[]> bytesCache = new HashMap<String, byte[]>();

    private HashMap<String, String> stringCache = new HashMap<String, String>();

    private HashMap<String, Thread> runningThreads = new HashMap<String, Thread>();

    private HashMap<String, ArrayList<ParamsEntity>> pendingRequest =
                    new HashMap<String, ArrayList<ParamsEntity>>();


    /**
     * handle connecting child thread to main thread
     */
    private Handler messageHandler = new Handler() {
        /**
         * @param msg of type Message function which will be called on load progress or load
         *        complete
         * 
         */
        @Override
        public void handleMessage(Message msg) {

            int type = msg.getWhat();
            DownloadRunnable runnable = (DownloadRunnable) msg.getObj();
            ParamsEntity params = runnable.getParams();
            if (type == COMPLETE) {
                ResultEntity result = runnable.getResult();
                runnable.reset();
                cancelThread(result.getUrl());
                if (pendingRequest.get(params.getUrl()) == null) {
                    params.getListener().onResult(result);
                } else {
                    ArrayList<ParamsEntity> requestParamsList = pendingRequest.get(params.getUrl());
                    for (ParamsEntity param : requestParamsList) {
                        param.getListener().onResult(result);
                    }
                    pendingRequest.remove(params.getUrl());
                }
                runnable = null;
            } else {
                if (pendingRequest.get(params.getUrl()) == null) {
                    params.getListener().onProgress(runnable.getProgress());
                } else {
                    ArrayList<ParamsEntity> requestParamsList = pendingRequest.get(params.getUrl());
                    for (ParamsEntity param : requestParamsList) {
                        param.getListener().onProgress(runnable.getProgress());
                    }
                }
            }
        }
    };

    /**
     * @param of type null
     * @return _instance of type DownLoadManager getter function for _instance
     */
    public synchronized static DownLoadManager get_instance() {
        _instance = (_instance == null) ? new DownLoadManager(new SingleTonEnforcer()) : _instance;
        return _instance;
    }

    /**
     * Constructor
     * 
     * @param singleTonEnforcer of type SingleTonEnforcer
     */
    public DownLoadManager(SingleTonEnforcer singleTonEnforcer) {}

    /**
     * @param params of type ParamsEntity
     * @return of type null function which will set the initial steps for the loading
     */
    public void getAsset(ParamsEntity params, boolean saveResult) {

        if (!saveResult) {
            loadAsset(params);
        } else if (bytesCache.get(params.getUrl()) == null
                        && stringCache.get(params.getUrl()) == null) {
            if (!checkPendingRequest(params)) {
                loadAsset(params);
            }
        } else {
            ResultEntity result = new ResultEntity("Sucsess", 200);
            if (params.getResultFormat() == ResultFormats.RAW) {
                result.setResultBytes(bytesCache.get(params.getUrl()));
            } else {
                result.setResultString(stringCache.get(params.getUrl()));
            }
            result.setUrl(params.getUrl());
            params.getListener().onResult(result);
        }
    }


    /**
     * @param params of type ParamsEntity
     * @return of type null function which will initiate loading of assets
     */
    private void loadAsset(ParamsEntity params) {
        try {
            Thread thread =
                            new Thread(new DownloadRunnable(params, messageHandler,
                                            getRequest(params)));
            runningThreads.put(params.getUrl(), thread);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param runnable of type DownloadRunnable
     * @return of type null function which will handle the complete of the runnable
     */
    public void observe(DownloadRunnable runnable, int type) {
        Message msg = new Message();
        msg.setWhat(type);
        msg.setObj(runnable);
        messageHandler.sendMessage(msg);
    }

    /**
     * @param key of type String
     * @return of type null function which will remove the running thread
     */
    public void cancelThread(String key) {
        try {
            Thread thread = runningThreads.get(key);
            if (thread.isAlive()) {
                thread.interrupt();
            }
            runningThreads.remove(key);
            thread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param params of type ParamsEntity
     * @return request of type HTTPUriRequest function which will create the request
     */
    @SuppressWarnings("deprecation")
    private HttpUriRequest getRequest(ParamsEntity params) throws Exception {
        HttpUriRequest request = null;
        switch (params.getRequestType()) {
            case RequestMethods.GET: {
                request = new HttpGet(params.getUrl() + ParamsUtils.getParamsString(params));
                ParamsUtils.addHeaders(request, params);
                break;
            }
            case RequestMethods.POST: {
                HttpPost postRequest = new HttpPost(params.getUrl());
                ParamsUtils.addHeaders(request, params);
                if (params.getParams() != null && !params.getParams().isEmpty()) {
                    postRequest.setEntity(new UrlEncodedFormEntity(params.getParams(), HTTP.UTF_8));
                }
                request = postRequest;
                break;
            }
            default:
                break;
        }
        return request;
    }

    /**
     * @param params of type ParamsEntity
     * @return of type Boolean function which will check for the pending request of the same asset
     */
    private boolean checkPendingRequest(ParamsEntity params) {
        boolean pending;
        ArrayList<ParamsEntity> requestParamsList;
        if (pendingRequest.get(params.getUrl()) != null) {
            pending = true;
            requestParamsList = pendingRequest.get(params.getUrl());
            requestParamsList.add(params);
            pendingRequest.put(params.getUrl(), requestParamsList);
        } else {
            pending = false;
            requestParamsList = new ArrayList<ParamsEntity>();
            requestParamsList.add(params);
            pendingRequest.put(params.getUrl(), requestParamsList);
        }
        return pending;
    }


    /**
     * Singleton Class
     */
    private static class SingleTonEnforcer {
        public SingleTonEnforcer() {

        }
    }

}
