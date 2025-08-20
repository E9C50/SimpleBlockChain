package com.fxcodeo.transaction;

import com.fxcodeo.bitcoin.Block;
import com.fxcodeo.utils.StringUtil;
import lombok.Data;

import java.security.PublicKey;

@Data
public class TransactionOutput {
    private String id;
    private PublicKey recipient;
    private double amount;
    private String parentTxId;

    public TransactionOutput(PublicKey recipient, double amount, String parentTxId) {
        this.recipient = recipient;
        this.amount = amount;
        this.parentTxId = parentTxId;
        this.id = Block.applySha256(
                StringUtil.getStringFromKey(recipient) +
                        amount +
                        parentTxId
        );
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey.equals(recipient);
    }
}