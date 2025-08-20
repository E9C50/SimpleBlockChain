package com.fxcodeo.network;

import com.fxcodeo.bitcoin.BitcoinSystem;
import com.fxcodeo.bitcoin.Block;
import com.fxcodeo.transaction.Transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class P2PNetwork {
    private List<Socket> peers = new ArrayList<>();
    private ServerSocket serverSocket;

    public void startServer(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    peers.add(clientSocket);
                    System.out.println("New peer connected: " + clientSocket.getInetAddress());

                    new Thread(new PeerHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }).start();
    }

    public void connectToPeer(String host, int port) throws IOException {
        Socket peerSocket = new Socket(host, port);
        peers.add(peerSocket);
        System.out.println("Connected to peer: " + host + ":" + port);

        new Thread(new PeerHandler(peerSocket)).start();
    }

    public void broadcastBlock(Block block) {
        for (Socket peer : peers) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(peer.getOutputStream());
                out.writeObject(new NetworkMessage(NetworkMessage.BLOCK, block));
                out.flush();
            } catch (IOException e) {
                System.err.println("Error broadcasting to peer: " + e.getMessage());
            }
        }
    }

    public void broadcastTransaction(Transaction transaction) {
        for (Socket peer : peers) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(peer.getOutputStream());
                out.writeObject(new NetworkMessage(NetworkMessage.TRANSACTION, transaction));
                out.flush();
            } catch (IOException e) {
                System.err.println("Error broadcasting to peer: " + e.getMessage());
            }
        }
    }

    private class PeerHandler implements Runnable {
        private Socket peerSocket;

        public PeerHandler(Socket socket) {
            this.peerSocket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(peerSocket.getInputStream());

                while (true) {
                    NetworkMessage message = (NetworkMessage) in.readObject();

                    switch (message.getType()) {
                        case NetworkMessage.BLOCK:
                            Block receivedBlock = (Block) message.getData();
                            // Validate and add to blockchain
                            if (isValidNewBlock(receivedBlock)) {
                                BitcoinSystem.blockchain.add(receivedBlock);
                                System.out.println("New block added from peer");
                            }
                            break;

                        case NetworkMessage.TRANSACTION:
                            Transaction receivedTx = (Transaction) message.getData();
                            // Validate and add to mempool
                            if (isValidTransaction(receivedTx)) {
                                // Add to mempool (not implemented in this simplified version)
                                System.out.println("New transaction received from peer");
                            }
                            break;

                        case NetworkMessage.REQUEST_BLOCKCHAIN:
                            // Send entire blockchain to requesting peer
                            ObjectOutputStream out = new ObjectOutputStream(peerSocket.getOutputStream());
                            out.writeObject(new NetworkMessage(
                                    NetworkMessage.BLOCKCHAIN,
                                    BitcoinSystem.blockchain
                            ));
                            out.flush();
                            break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Peer disconnected: " + e.getMessage());
                peers.remove(peerSocket);
            }
        }

        private boolean isValidNewBlock(Block newBlock) {
            Block latestBlock = BitcoinSystem.blockchain.get(BitcoinSystem.blockchain.size() - 1);

            return latestBlock.getHash().equals(newBlock.getPreviousHash()) &&
                    newBlock.getHash().equals(newBlock.calculateHash()) &&
                    newBlock.getHash().substring(0, BitcoinSystem.difficulty)
                            .equals(new String(new char[BitcoinSystem.difficulty]).replace('\0', '0'));
        }

        private boolean isValidTransaction(Transaction tx) {
            // Simplified validation - in a real system, this would be more thorough
            return tx.verifySignature();
        }
    }
}