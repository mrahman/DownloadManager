package com.mrahman.downloadmanager.util.thread;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Message {
    private int what;
    private Object obj;
}
