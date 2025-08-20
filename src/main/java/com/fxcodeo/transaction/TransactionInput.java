package com.fxcodeo.transaction;

import lombok.Data;

@Data
public class TransactionInput {
    private String txOutputId;
    private TransactionOutput UTXO;

    public TransactionInput(String txOutputId) {
        this.txOutputId = txOutputId;
    }

}