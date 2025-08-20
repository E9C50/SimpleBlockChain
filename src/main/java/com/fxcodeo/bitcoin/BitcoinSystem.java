package com.fxcodeo.bitcoin;

import com.fxcodeo.transaction.Transaction;
import com.fxcodeo.transaction.TransactionInput;
import com.fxcodeo.transaction.TransactionOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class BitcoinSystem {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 5;

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);

        for (Transaction tx : newBlock.getTransactions()) {
            for (TransactionInput input : tx.getInputs()) {
                BitcoinSystem.UTXOs.remove(input.getTxOutputId());
            }

            for (TransactionOutput output : tx.getOutputs()) {
                BitcoinSystem.UTXOs.put(output.getId(), output);
            }
        }

        blockchain.add(newBlock);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }

        System.out.println("Blockchain is valid");
        return true;
    }
}