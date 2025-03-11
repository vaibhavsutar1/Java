import java.io.*;
import java.util.Scanner;

public class CaesarCipher {
    
    public static String encryption(String message, int key) {
        StringBuilder encrypt = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (Character.isLetter(ch)) {
                int base = Character.isLowerCase(ch) ? 'a' : 'A';
                encrypt.append((char) ((ch - base + key) % 26 + base));
            } else {
                encrypt.append(ch);
            }
        }
        return encrypt.toString();
    }

    public static String decryption(String message, int key) {
        StringBuilder decrypt = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (Character.isLetter(ch)) {
                int base = Character.isLowerCase(ch) ? 'a' : 'A';
                decrypt.append((char) ((ch - base - key + 26) % 26 + base));
            } else {
                decrypt.append(ch);
            }
        }
        return decrypt.toString();
    }

    public static void processFile(String inputFile, String outputFile, int key, String mode) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

            String processedContent;
            if (mode.equalsIgnoreCase("encrypt")) {
                processedContent = encryption(content.toString(), key);
            } else if (mode.equalsIgnoreCase("decrypt")) {
                processedContent = decryption(content.toString(), key);
            } else {
                System.out.println("Invalid mode! Use 'encrypt' or 'decrypt'.");
                return;
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(processedContent);
            writer.close();

            System.out.println("File saved successfully: " + outputFile);
        } catch (IOException e) {
            System.out.println("Error processing file: " + inputFile);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the input file name: ");
        String inputFile = scanner.nextLine().trim();

        System.out.print("Enter the key: ");
        int key = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter the encryption file name: ");
        String encryptedFile = scanner.nextLine().trim();
        processFile(inputFile, encryptedFile, key, "encrypt");

        System.out.print("Enter the decrypted file name: ");
        String decryptedFile = scanner.nextLine().trim();
        processFile(encryptedFile, decryptedFile, key, "decrypt");

        scanner.close();
    }
}

