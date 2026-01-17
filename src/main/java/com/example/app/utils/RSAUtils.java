package com.example.app.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RSAUtils {

    /**
     * RSA encrypt using raw modular exponentiation to match the legacy JS
     * implementation.
     * The JS code treats the password as a byte array (little-endian), splits it
     * into chunks based on modulus size,
     * and performs raw m^e mod n.
     *
     * @param plainText Text to encrypt
     * @param modulus   Hex string of modulus
     * @param exponent  Hex string of exponent
     * @return Hex string of the encrypted blocks, separated by space
     */
    public static String encrypt(String plainText, String modulus, String exponent) {
        BigInteger m = new BigInteger(modulus, 16);
        BigInteger e = new BigInteger(exponent, 16);

        // Calculate chunkSize based on the JS logic: key.chunkSize = 2 * biHighIndex(m)
        // biHighIndex is (bitLength - 1) / 16
        int bitLength = m.bitLength();
        int biHighIndex = (bitLength - 1) / 16;
        int chunkSize = 2 * biHighIndex; // in bytes/chars

        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Modulus is too small for this encryption scheme.");
        }

        List<Integer> chars = new ArrayList<>();
        for (char c : plainText.toCharArray()) {
            chars.add((int) c);
        }

        // Pad with zeros to multiple of chunkSize
        while (chars.size() % chunkSize != 0) {
            chars.add(0);
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < chars.size(); i += chunkSize) {
            // Construct the block. JS packs chars into little-endian number.
            // c0 + c1*2^8 + c2*2^16 ...
            byte[] blockBytes = new byte[chunkSize];
            for (int j = 0; j < chunkSize; j++) {
                blockBytes[j] = chars.get(i + j).byteValue();
            }

            // Reverse for BigInteger (which expects Big-Endian)
            for (int k = 0; k < blockBytes.length / 2; k++) {
                byte temp = blockBytes[k];
                blockBytes[k] = blockBytes[blockBytes.length - 1 - k];
                blockBytes[blockBytes.length - 1 - k] = temp;
            }

            BigInteger block = new BigInteger(1, blockBytes);
            BigInteger encrypted = block.modPow(e, m);

            String hex = encrypted.toString(16);

            // Pad hex string with leading zeros to multiple of 4 chars (JS digitToHex
            // behavior)
            if (hex.length() % 4 != 0) {
                int pad = 4 - (hex.length() % 4);
                StringBuilder sb = new StringBuilder();
                for (int p = 0; p < pad; p++)
                    sb.append('0');
                sb.append(hex);
                hex = sb.toString();
            }

            result.append(hex).append(" ");
        }

        return result.toString().trim();
    }
}
