package com.fxcodeo.transaction;

import com.fxcodeo.bitcoin.Block;
import com.fxcodeo.utils.StringUtil;
import lombok.Data;

import java.security.*;
import java.util.ArrayList;

@Data
public class Transaction {
    private final String txId;
    private final PublicKey sender;
    private final PublicKey recipient;
    private final double amount;
    private byte[] signature;
    private ArrayList<TransactionInput> inputs = new ArrayList<>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

    public Transaction(PublicKey sender, PublicKey recipient, double amount, ArrayList<TransactionInput> inputs) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.inputs = inputs;
        this.txId = calculateHash();
    }

    private String calculateHash() {
        return Block.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        amount
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                amount;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                amount;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }
}


