package com.yourname.passwardapp.util;

import com.yourname.passwardapp.model.PasswordEntry;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern; // import を追加
import java.util.stream.Collectors;

public class DatabaseManager {

    private final List<PasswordEntry> passwordList;
    private static final String FILE_NAME = "passwords.enc";
    private static final String DELIMITER = "|||";

    public DatabaseManager(String masterPassword) {
        this.passwordList = new ArrayList<>();
        loadDataFromFile(masterPassword);
    }

    private void loadDataFromFile(String masterPassword) {
        Path path = Paths.get(FILE_NAME);
        if (!Files.exists(path)) {
            System.out.println("データファイルが存在しません。");
            return;
        }

        try {
            byte[] fileBytes = Files.readAllBytes(path);
            byte[] salt = Arrays.copyOfRange(fileBytes, 0, 16);
            byte[] encryptedData = Arrays.copyOfRange(fileBytes, 16, fileBytes.length);

            SecretKey key = CryptoUtils.generateKey(masterPassword, salt);
            String decryptedText = CryptoUtils.decrypt(Base64.getEncoder().encodeToString(encryptedData), key);

            String[] lines = decryptedText.split(System.lineSeparator());
            for (String line : lines) {
                if (!line.isEmpty()) {
                    // Pattern.quote() を使って正しく分割
                    String[] parts = line.split(Pattern.quote(DELIMITER), 3);
                    if (parts.length == 3) {
                        passwordList.add(new PasswordEntry(parts[0], parts[1], parts[2]));
                    }
                }
            }
            System.out.println("データをファイルから読み込みました。");
        } catch (Exception e) {
            System.err.println("データの復号に失敗しました。マスターパスワードが違う可能性があります。");
            passwordList.clear();
        }
    }

    public void saveDataToFile(String masterPassword) {
        if (masterPassword == null || masterPassword.isEmpty()) return;

        try {
            String plainText = passwordList.stream()
                    .map(p -> String.join(DELIMITER, p.getServiceName(), p.getUsername(), p.getPassword()))
                    .collect(Collectors.joining(System.lineSeparator()));

            if (plainText.isEmpty()) {
                Files.deleteIfExists(Paths.get(FILE_NAME));
                System.out.println("データが空のため、ファイルを削除しました。");
                return;
            }

            byte[] salt = CryptoUtils.getRandomSalt();
            SecretKey key = CryptoUtils.generateKey(masterPassword, salt);
            String encryptedText = CryptoUtils.encrypt(plainText, key);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] fileBytes = new byte[salt.length + encryptedBytes.length];
            System.arraycopy(salt, 0, fileBytes, 0, salt.length);
            System.arraycopy(encryptedBytes, 0, fileBytes, salt.length, encryptedBytes.length);

            Files.write(Paths.get(FILE_NAME), fileBytes);
            System.out.println("データをファイルに保存しました。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePassword(PasswordEntry entry) {
        passwordList.add(entry);
    }

    public void deletePassword(PasswordEntry entry) {
        passwordList.remove(entry);
    }

    public List<PasswordEntry> getAllPasswords() {
        return new ArrayList<>(this.passwordList);
    }
}