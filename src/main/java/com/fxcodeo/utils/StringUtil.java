package com.fxcodeo.utils;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class StringUtil {
 
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            dsa.update(input.getBytes());
            return dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initVerify(publicKey);
            dsa.update(data.getBytes());
            return dsa.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey getPublicKeyFromString(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            return kf.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}