package com.fxcodeo.utils;

import java.math.BigInteger;
import java.util.Arrays;

public class Base58 {
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(58);

    public static String encode(byte[] input) {
        BigInteger value = new BigInteger(1, input);
        StringBuilder sb = new StringBuilder();

        while (value.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = value.divideAndRemainder(BASE);
            sb.append(ALPHABET.charAt(divmod[1].intValue()));
            value = divmod[0];
        }

        // 处理前导零
        for (byte b : input) {
            if (b == 0) sb.append(ALPHABET.charAt(0));
            else break;
        }

        return sb.reverse().toString();
    }

    public static byte[] decode(String input) {
        BigInteger value = BigInteger.ZERO;

        for (char c : input.toCharArray()) {
            int digit = ALPHABET.indexOf(c);
            if (digit == -1) throw new IllegalArgumentException("Invalid Base58 character: " + c);
            value = value.multiply(BASE).add(BigInteger.valueOf(digit));
        }

        byte[] bytes = value.toByteArray();
        // 处理前导零
        int zeros = 0;
        while (zeros < input.length() && input.charAt(zeros) == ALPHABET.charAt(0)) zeros++;

        byte[] result = new byte[zeros + bytes.length];
        Arrays.fill(result, 0, zeros, (byte) 0);
        System.arraycopy(bytes, 0, result, zeros, bytes.length);
        return result;
    }
}