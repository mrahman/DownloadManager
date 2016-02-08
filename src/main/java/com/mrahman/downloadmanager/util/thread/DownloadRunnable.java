/**
 * 
 */
package com.mrahman.downloadmanager.util.thread;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.mrahman.downloadmanager.DownLoadManager;
import com.mrahman.downloadmanager.data.ParamsEntity;
import com.mrahman.downloadmanager.data.ProgressEntity;
import com.mrahman.downloadmanager.data.ResultEntity;
import com.mrahman.downloadmanager.util.param.ResultFormats;

/**
 * @author mizanur.rahman
 *
 */
@SuppressWarnings("deprecation")
public class DownloadRunnable implements Runnable {
    private ParamsEntity params;
    private Handler listener;
    private ResultEntity result;
    private ProgressEntity progress;
    private HttpUriRequest request;

    /**
     * @param of type null
     * @return params of type ParamsEntity getter function for params
     */
    public ParamsEntity getParams() {
        return params;
    }

    /**
     * @param of type null
     * @return result of type ResultEntity getter function for result
     */
    public ResultEntity getResult() {
        return result;
    }

    /**
     * @param of type null
     * @return progress of type ProgressEntity getter function for progress
     */
    public ProgressEntity getProgress() {
        return progress;
    }

    /**
	 * 
	 */
    public DownloadRunnable(ParamsEntity params, Handler handler, HttpUriRequest request) {
        this.params = params;
        this.listener = handler;
        this.request = request;
    }

    /**
     * @param of type null
     * @return of type null function which will load the file
     */
    public void run() {
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;
        try {

            httpResponse = client.execute(request);
            result =
                            new ResultEntity(httpResponse.getStatusLine().getReasonPhrase(),
                                            httpResponse.getStatusLine().getStatusCode());
            result.setUrl(params.getUrl());
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {

                InputStream instream = entity.getContent();

                if (httpResponse.getHeaders("Content-Length").length > 0) {
                    progress = (progress == null) ? new ProgressEntity() : progress;
                    Header header = httpResponse.getHeaders("Content-Length")[0];
                    progress.setTotalBytes(Float.valueOf(header.getValue()));
                    progress.setUrl(params.getUrl());
                }

                if (params.getResultFormat() == ResultFormats.RAW) {
                    result.setResultBytes(IOUtils.toByteArray(instream));
                } else {
                    result.setResultString(convertStreamToString(instream));
                }
                instream.close();
                client.getConnectionManager().shutdown();
                Message msg = new Message();
                msg.setObj(this);
                msg.setWhat(DownLoadManager.COMPLETE);
                listener.sendMessage(msg);
            }
        } catch (Exception e) {
            client.getConnectionManager().shutdown();
            System.out.print(e.getMessage());
        }

    }

    /**
     * @param is of type InputStream
     * @return of type String function which convert the stream to String
     */
    private static String convertStreamToString(InputStream inputStream) {
        String result = "";
        try {
            StringBuffer buffer = new StringBuffer();
            byte[] data = new byte[256];
            int len = 0;
            while (-1 != (len = inputStream.read(data))) {
                buffer.append(new String(data, 0, len));
            }
            result = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param of type null function which will reset the runnable
     */
    public void reset() {
        params = null;
        listener = null;
        result = null;
        request = null;
        progress = null;
    }

}
