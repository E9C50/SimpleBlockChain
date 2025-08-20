package com.fxcodeo.bitcoin;

import com.fxcodeo.transaction.Transaction;
import com.fxcodeo.transaction.TransactionInput;
import com.fxcodeo.transaction.TransactionOutput;
import com.fxcodeo.utils.Base58;
import lombok.Data;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;

@Data
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getBitcoinAddress() {
        try {
            // 1. 获取公钥SHA-256哈希
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] pubKeyHash = sha.digest(publicKey.getEncoded());

            // 2. 添加版本字节（主网为0x00）
            byte[] versionedHash = new byte[pubKeyHash.length + 1];
            versionedHash[0] = 0x00; // 主网版本号
            System.arraycopy(pubKeyHash, 0, versionedHash, 1, pubKeyHash.length);

            // 3. 计算校验和（双SHA-256）
            byte[] checksum = sha.digest(sha.digest(versionedHash));

            // 4. 组合并Base58编码
            byte[] addressBytes = new byte[versionedHash.length + 4];
            System.arraycopy(versionedHash, 0, addressBytes, 0, versionedHash.length);
            System.arraycopy(checksum, 0, addressBytes, versionedHash.length, 4);

            return Base58.encode(addressBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getBalance() {
        double total = 0;
        UTXOs.clear();

        for (TransactionOutput UTXO : BitcoinSystem.UTXOs.values()) {
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.getId(), UTXO);
                total += UTXO.getAmount();
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, double amount) {
        // 1. 获取发送者余额并检查
        double balance = getBalance();
        if (balance < amount) {
            System.out.println("Not enough funds to send transaction.");
            return null;
        }

        // 2. 收集足够的UTXO作为输入
        ArrayList<TransactionInput> inputs = new ArrayList<>();
        double total = 0;

        for (TransactionOutput UTXO : UTXOs.values()) {
            total += UTXO.getAmount();
            inputs.add(new TransactionInput(UTXO.getId()));
            if (total >= amount) break; // 收集足够金额就停止
        }

        // 3. 创建新交易
        Transaction newTransaction = new Transaction(publicKey, recipient, amount, inputs);
        newTransaction.generateSignature(privateKey);

        // 4. 创建交易后立即从全局UTXOs中移除已使用的UTXO
        for (TransactionInput input : inputs) {
            BitcoinSystem.UTXOs.remove(input.getTxOutputId());
        }

        // 5. 创建输出
        ArrayList<TransactionOutput> outputs = new ArrayList<>();

        // 给接收者的输出
        outputs.add(new TransactionOutput(recipient, amount, newTransaction.getTxId()));

        // 找零给发送者(如果有)
        if (total > amount) {
            outputs.add(new TransactionOutput(publicKey, total - amount, newTransaction.getTxId()));
        }

        newTransaction.setOutputs(outputs);

        // 6. 更新UTXO集合
        // 先移除已使用的UTXO
        for (TransactionInput input : inputs) {
            UTXOs.remove(input.getTxOutputId());
        }

        // 添加新创建的UTXO
        for (TransactionOutput output : outputs) {
            BitcoinSystem.UTXOs.put(output.getId(), output);
        }

        // 7. 更新本地钱包的UTXO缓存
        UTXOs.clear(); // 清空缓存
        getBalance();  // 重新加载UTXO

        return newTransaction;
    }
}