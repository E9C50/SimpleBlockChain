package com.fxcodeo.bitcoin;

import com.fxcodeo.transaction.Transaction;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

@Data
public class Block {
    private final List<Transaction> transactions = new ArrayList<>();
    private final String previousHash;
    private final long timeStamp;

    private String merkleRoot;
    private String hash;
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String dataToHash = previousHash + timeStamp + nonce + merkleRoot;
        return applySha256(dataToHash);
    }

    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mineBlock(int difficulty) {
        merkleRoot = getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }

        System.out.println("Block mined: " + hash);
    }

    private String getMerkleRoot(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return "";
        }

        List<String> treeLayer = new ArrayList<>();
        for (Transaction tx : transactions) {
            treeLayer.add(tx.getTxId());
        }

        while (treeLayer.size() > 1) {
            List<String> newLayer = new ArrayList<>();

            for (int i = 0; i < treeLayer.size(); i += 2) {
                String left = treeLayer.get(i);
                String right = (i + 1 < treeLayer.size()) ? treeLayer.get(i + 1) : left;
                newLayer.add(applySha256(left + right));
            }

            treeLayer = newLayer;
        }

        return treeLayer.get(0);
    }

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }
}