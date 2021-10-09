package com.example.cqrs.keystore;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class LogstashKeyStoreManager {

    public static final String KEY_STORE = "D:\\ws\\logstash-modified\\src\\main\\resources\\logstash.keystore";
    public static final String KEYSTORE_PASSWORD = "password";
    public static final String ENTRY_PASSWORD = "password";
    public static final String LOGSTASH_PREFIX_FOR_ALIAS = "urn:logstash:secret:v1:";
    public static final String ALIAS = "db-encryption-secret";

    private static Map<String, String> entries = new HashMap<>() {{
        put("keystore.seed", "keystore.seed");
        put(ALIAS, "ABCVDEF");
    }};

    public static void main(String[] args) throws Exception {
        loadKeyStore(KEYSTORE_PASSWORD.toCharArray());
        readKeyStore(KEYSTORE_PASSWORD.toCharArray());
    }

    private static void loadKeyStore(char[] keystorePassword) throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, keystorePassword);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(ENTRY_PASSWORD.toCharArray());
        try (FileOutputStream fos = new FileOutputStream(KEY_STORE)) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                PBEKeySpec pbeKeySpec = new PBEKeySpec(Base64.getEncoder().encodeToString(entry.getValue().getBytes()).toCharArray());
                SecretKey secretKey = factory.generateSecret(pbeKeySpec);
                KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
                ks.setEntry(LOGSTASH_PREFIX_FOR_ALIAS + entry.getKey(), secret, password);
            }
            ks.store(fos, keystorePassword);
        }
        Key ssoSigningKey = ks.getKey(LOGSTASH_PREFIX_FOR_ALIAS + ALIAS, ENTRY_PASSWORD.toCharArray());
        System.out.println("This is it : " + new String(ssoSigningKey.getEncoded()));
    }

    private static void readKeyStore(char[] keystorePassword) throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(KEY_STORE), keystorePassword);
        Key alias = ks.getKey(LOGSTASH_PREFIX_FOR_ALIAS + "db-encryption-secret", ENTRY_PASSWORD.toCharArray());
        Key seed = ks.getKey(LOGSTASH_PREFIX_FOR_ALIAS + "keystore.seed", ENTRY_PASSWORD.toCharArray());

        System.out.println("Alias : " + new String(Base64.getDecoder().decode(alias.getEncoded())));
        System.out.println("Seed : " + new String(Base64.getDecoder().decode(seed.getEncoded())));
    }
}
