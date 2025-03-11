import java.io.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class DESFileEncryption {

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56); 
        return keyGenerator.generateKey();
    }

    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }
    public static String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to read file " + fileName);
            return null;
        }
        return content.toString().trim();
    }

    public static void writeFile(String fileName, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(data);
        } catch (IOException e) {
            System.out.println("Error: Unable to write to file " + fileName);
        }
    }

    public static void main(String[] args) {
        try {
            SecretKey secretKey = generateKey();
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("\nDES File Encryption Program");
                System.out.println("1. Encrypt File");
                System.out.println("2. Decrypt File");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.readLine().trim();

                if (choice.equals("1")) {
                    System.out.print("Enter input file name: ");
                    String inputFile = scanner.readLine().trim();
                    System.out.print("Enter output encrypted file name: ");
                    String encryptedFile = scanner.readLine().trim();

                    String plainText = readFile(inputFile);
                    if (plainText != null) {
                        String encryptedText = encrypt(plainText, secretKey);
                        writeFile(encryptedFile, encryptedText);
                        System.out.println("Encryption completed! Check " + encryptedFile);
                    }

                } else if (choice.equals("2")) {
                    System.out.print("Enter encrypted file name: ");
                    String encryptedFile = scanner.readLine().trim();
                    System.out.print("Enter output decrypted file name: ");
                    String decryptedFile = scanner.readLine().trim();

                    String encryptedText = readFile(encryptedFile);
                    if (encryptedText != null) {
                        String decryptedText = decrypt(encryptedText, secretKey);
                        writeFile(decryptedFile, decryptedText);
                        System.out.println("Decryption completed! Check " + decryptedFile);
                    }

                } else if (choice.equals("3")) {
                    System.out.println("Exiting program. Goodbye!");
                    break;

                } else {
                    System.out.println("Invalid choice! Please enter 1, 2, or 3.");
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

