package com.yourname.passwardapp.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoUtils {

    // 暗号化アルゴリズムの指定 (AES/GCMモード)
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    // タグ長 (GCMモードで推奨される128ビット)
    private static final int TAG_LENGTH_BIT = 128;
    // 初期化ベクトル(IV)の長さ (GCMモードで推奨される12バイト)
    private static final int IV_LENGTH_BYTE = 12;
    // ソルトの長さ
    private static final int SALT_LENGTH_BYTE = 16;
    // キーを生成するためのアルゴリズム
    private static final String SECRET_KEY_ALGO = "PBKDF2WithHmacSHA256";

    /**
     * ランダムなソルトを生成します。
     * ソルトはキー生成時に一度だけ作成し、暗号化データと一緒に保存する必要があります。
     */
    public static byte[] getRandomSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTE];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * マスターパスワードとソルトから、安全な暗号化キーを生成します。
     * @param password マスターパスワード
     * @param salt ソルト
     * @return 生成されたSecretKey
     */
    public static SecretKey generateKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGO);
        // パスワードをキーに変換するための設定 (イテレーション回数: 65536, キー長: 256ビット)
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    /**
     * 文字列を暗号化します。
     * @param plainText 暗号化したい文字列
     * @param secretKey generateKeyで生成したキー
     * @return Base64エンコードされた暗号文（IV含む）
     */
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        // 1. IVをランダムに生成
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        // 2. 実際に暗号化
        byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

        // 3. IVと暗号文を結合して、Base64で文字列に変換
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    /**
     * 暗号文を復号します。
     * @param encryptedText Base64エンコードされた暗号文
     * @param secretKey generateKeyで生成したキー
     * @return 復号された元の文字列
     */
    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        // 1. Base64文字列を元のバイト配列に戻す
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);

        // 2. IVと暗号文を分離
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        // 3. 実際に復号
        byte[] plainTextBytes = cipher.doFinal(cipherText);
        return new String(plainTextBytes, "UTF-8");
    }
}



