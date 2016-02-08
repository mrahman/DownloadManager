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
public class ProgressEntity {

    private float progress;

    private String url;

    private float totalBytes;

    private long bytesLoaded;

}
