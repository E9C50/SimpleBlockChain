package com.fxcodeo.client;

import com.fxcodeo.bitcoin.BitcoinSystem;
import com.fxcodeo.bitcoin.Block;
import com.fxcodeo.bitcoin.Wallet;
import com.fxcodeo.network.P2PNetwork;
import com.fxcodeo.transaction.Transaction;
import com.fxcodeo.transaction.TransactionInput;
import com.fxcodeo.transaction.TransactionOutput;

import java.util.ArrayList;

public class BitcoinClient {

    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        // Setup wallets
        walletA = new Wallet();
        walletB = new Wallet();

        // Create genesis transaction
        genesisTransaction = new Transaction(
                new Wallet().getPublicKey(), // Coinbase
                walletA.getPublicKey(),
                100,
                new ArrayList<TransactionInput>()
        );
        genesisTransaction.generateSignature(new Wallet().getPrivateKey());
        genesisTransaction.setOutputs(new ArrayList<TransactionOutput>() {{
            add(new TransactionOutput(
                    walletA.getPublicKey(),
                    100,
                    genesisTransaction.getTxId()
            ));
        }});

        // Store genesis transaction in UTXOs
        BitcoinSystem.UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        System.out.println("Creating and mining Genesis block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        BitcoinSystem.addBlock(genesis);

        // Testing
        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance: " + walletA.getBalance());
        System.out.println("\nWalletA is attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40));
        BitcoinSystem.addBlock(block1);
        System.out.println("\nWalletA's balance: " + walletA.getBalance());
        System.out.println("WalletB's balance: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA is attempting to send funds (1000) to WalletB...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000));
        BitcoinSystem.addBlock(block2);
        System.out.println("\nWalletA's balance: " + walletA.getBalance());
        System.out.println("WalletB's balance: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20));
        BitcoinSystem.addBlock(block3);
        System.out.println("\nWalletA's balance: " + walletA.getBalance());
        System.out.println("WalletB's balance: " + walletB.getBalance());

        BitcoinSystem.isChainValid();

        // Start P2P network
        P2PNetwork network = new P2PNetwork();
        network.startServer(8080);
        // In a real system, you would connect to other peers here

        System.out.println();
    }

//    public static void main(String[] args) throws IOException {
//        P2PNetwork network = new P2PNetwork();
//        network.startServer(8080);
//
//        network.connectToPeer("localhost", 8081);
//
//        new CommandLineInterface().start();
//    }
}