package com.fxcodeo.network;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class NetworkMessage implements Serializable {
    public static final int BLOCK = 0;
    public static final int TRANSACTION = 1;
    public static final int REQUEST_BLOCKCHAIN = 2;
    public static final int BLOCKCHAIN = 3;

    private final int type;
    private final Object data;

    public NetworkMessage(int type, Object data) {
        this.type = type;
        this.data = data;
    }

}