/**
 * 
 */
package com.mrahman.downloadmanager.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mizanur.rahman
 *
 */
@Getter
@Setter
@ToString
public class ResultEntity {

    private String url;

    private String response;

    private int responseCode;

    private String resultString;

    private byte[] resultBytes;

    /**
     * Constructor
     */
    public ResultEntity(String response, int responseCode) {
        this.response = response;
        this.responseCode = responseCode;
    }

}
