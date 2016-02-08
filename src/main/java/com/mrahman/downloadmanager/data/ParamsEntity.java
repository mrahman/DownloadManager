/**
 * 
 */
package com.mrahman.downloadmanager.data;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mrahman.downloadmanager.DownloadListener;


/**
 * @author mizanur.rahman
 *
 */
@Getter
@Setter
@ToString
public class ParamsEntity {


    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;

    private int requestType;

    private String resultFormat;
    private String url;

    private DownloadListener listener;

    /**
     * @param key of type String
     * @param value of type String function which will add the parameter to params
     * 
     */
    public void addParam(String key, String value) {
        params = (params == null) ? new ArrayList<NameValuePair>() : params;
        params.add(new BasicNameValuePair(key, value));
    }

    /**
     * @param key of type String
     * @param value of type String function which will add the header to headers
     */
    public void addHeader(String key, String value) {
        headers = (headers == null) ? new ArrayList<NameValuePair>() : headers;
        headers.add(new BasicNameValuePair(key, value));
    }
}
