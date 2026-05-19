package com.example.app.storage;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.prefs.Preferences;

@Slf4j
public class CredentialStorage {

    private static final String PREF_NODE = "javafx-app-credentials";
    private static final String KEY_USERNAME = "saved_username";
    private static final String KEY_PASSWORD = "saved_password";
    private static final String KEY_REMEMBER = "remember_me";
    private static final String KEY_SALT = "credential_salt";

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private final Preferences prefs;
    private final SecureRandom random = new SecureRandom();

    public CredentialStorage() {
        prefs = Preferences.userNodeForPackage(CredentialStorage.class).node(PREF_NODE);
    }

    public void saveCredentials(String username, String password, boolean rememberMe) {
        if (!rememberMe) {
            clearCredentials();
            return;
        }

        prefs.put(KEY_USERNAME, username);
        prefs.putBoolean(KEY_REMEMBER, true);

        try {
            byte[] salt = generateSalt();
            prefs.put(KEY_SALT, encodeBase64(salt));

            SecretKey key = deriveKey(getMachineFingerprint(), salt);
            byte[] encrypted = encrypt(password, key);
            prefs.put(KEY_PASSWORD, encodeBase64(encrypted));
        } catch (Exception e) {
            log.error("Failed to save encrypted credentials", e);
            clearCredentials();
        }
    }

    public String loadUsername() {
        if (!isRememberMe()) return null;
        return prefs.get(KEY_USERNAME, null);
    }

    public String loadPassword() {
        if (!isRememberMe()) return null;
        try {
            String saltB64 = prefs.get(KEY_SALT, null);
            String encryptedB64 = prefs.get(KEY_PASSWORD, null);
            if (saltB64 == null || encryptedB64 == null) return null;

            byte[] salt = decodeBase64(saltB64);
            SecretKey key = deriveKey(getMachineFingerprint(), salt);
            byte[] encrypted = decodeBase64(encryptedB64);
            return decrypt(encrypted, key);
        } catch (Exception e) {
            log.error("Failed to load encrypted credentials", e);
            return null;
        }
    }

    public boolean isRememberMe() {
        return prefs.getBoolean(KEY_REMEMBER, false);
    }

    public void clearCredentials() {
        prefs.remove(KEY_USERNAME);
        prefs.remove(KEY_PASSWORD);
        prefs.remove(KEY_REMEMBER);
        prefs.remove(KEY_SALT);
    }

    private byte[] encrypt(String plaintext, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        return ByteBuffer.allocate(iv.length + ciphertext.length)
                .put(iv)
                .put(ciphertext)
                .array();
    }

    private String decrypt(byte[] data, SecretKey key) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte[] iv = new byte[GCM_IV_LENGTH];
        buffer.get(iv);
        byte[] ciphertext = new byte[buffer.remaining()];
        buffer.get(ciphertext);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        return new String(cipher.doFinal(ciphertext));
    }

    private SecretKey deriveKey(String fingerprint, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(fingerprint.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private String getMachineFingerprint() {
        return System.getProperty("user.home", "unknown") + "|" + System.getProperty("user.name", "unknown");
    }

    private String encodeBase64(byte[] data) {
        return java.util.Base64.getEncoder().encodeToString(data);
    }

    private byte[] decodeBase64(String encoded) {
        return java.util.Base64.getDecoder().decode(encoded);
    }
}
