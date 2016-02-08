/**
 * 
 */
package com.mrahman.downloadmanager.util.param;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.mrahman.downloadmanager.data.ParamsEntity;


/**
 * @author mizanur.rahman
 *
 */
public class ParamsUtils {

    /**
     * @param paramsEntity of type ParamsEntity
     * @return combinedParams of type String function which will create the params String
     */
    public static String getParamsString(ParamsEntity paramsEntity) throws Exception {
        ArrayList<NameValuePair> _params = paramsEntity.getParams();
        String combinedParams = "";
        if (_params != null) {
            if (!_params.isEmpty()) {
                combinedParams += "?";
                for (NameValuePair p : _params) {
                    String paramString =
                                    p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                    if (combinedParams.length() > 1) {
                        combinedParams += "&" + paramString;
                    } else {
                        combinedParams += paramString;
                    }
                }
            }
        }
        return combinedParams;
    }

    /**
     * @param paramsEntity of type ParamsEntity
     * @param resquest of type HTTPUriRequest
     * @return of type null
     */
    public static void addHeaders(HttpUriRequest request, ParamsEntity params) {
        ArrayList<NameValuePair> headers = params.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (NameValuePair h : headers) {
                request.addHeader(h.getName(), h.getValue());
            }
        }
    }

}
