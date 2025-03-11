import java.io.*;
import java.util.*;

public class ColumnarCipher {
    private static char[][] colMatrix; 

    public static String encryptMessage(String msg, String key) {
        msg = msg.replace(" ", ""); 
        int col = key.length();
        int row = (int) Math.ceil((double) msg.length() / col);

        int padding = (row * col) - msg.length();
        StringBuilder paddedMsg = new StringBuilder(msg);
        for (int i = 0; i < padding; i++) {
            paddedMsg.append('_');
        }

        colMatrix = new char[row][col];
        int index = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                colMatrix[i][j] = paddedMsg.charAt(index++);
            }
        }

        System.out.println("\nColumnar Matrix:");
        displayMatrix(colMatrix, key);

        char[] sortedKey = key.toCharArray();
        Arrays.sort(sortedKey);

        StringBuilder cipherText = new StringBuilder();
        for (char c : sortedKey) {
            int colIndex = key.indexOf(c);
            for (int i = 0; i < row; i++) {
                cipherText.append(colMatrix[i][colIndex]);
            }
        }
        return cipherText.toString();
    }

    public static String decryptMessage(String cipher, String key) {
        int col = key.length();
        int row = (int) Math.ceil((double) cipher.length() / col);
        char[][] matrix = new char[row][col];

        char[] sortedKey = key.toCharArray();
        Arrays.sort(sortedKey);

        int index = 0;
        for (char c : sortedKey) {
            int colIndex = key.indexOf(c);
            for (int i = 0; i < row; i++) {
                matrix[i][colIndex] = cipher.charAt(index++);
            }
        }

        StringBuilder plainText = new StringBuilder();
        for (char[] rowArray : matrix) {
            for (char ch : rowArray) {
                plainText.append(ch);
            }
        }

        return plainText.toString().replace("_", ""); 
    }

    public static void displayMatrix(char[][] matrix, String key) {
        System.out.println("Key: " + key);
        for (char ch : key.toCharArray()) {
            System.out.print(ch + " ");
        }
        System.out.println("\n" + "- ".repeat(key.length()));

        for (char[] row : matrix) {
            for (char ch : row) {
                System.out.print(ch + " ");
            }
            System.out.println();
        }
    }

    public static String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error: File not found or cannot be read.");
            return null;
        }
        return content.toString();
    }

    public static void writeFile(String fileName, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(data);
        } catch (IOException e) {
            System.out.println("Error: Unable to write to file.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter encryption key: ");
        String key = scanner.nextLine().trim();

        while (true) {
            System.out.println("\nColumnar Cipher Program");
            System.out.println("1. Encrypt Text from file");
            System.out.println("2. Decrypt Text from file");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Enter input file name: ");
                String inputFile = scanner.nextLine().trim();
                System.out.print("Enter output encrypted file name: ");
                String encryptedFile = scanner.nextLine().trim();

                String plainText = readFile(inputFile);
                if (plainText != null) {
                    String encryptedText = encryptMessage(plainText, key);
                    writeFile(encryptedFile, encryptedText);
                    System.out.println("Encryption completed! Check " + encryptedFile);
                }

            } else if (choice.equals("2")) {
                System.out.print("Enter encrypted file name: ");
                String encryptedFile = scanner.nextLine().trim();
                System.out.print("Enter output decrypted file name: ");
                String decryptedFile = scanner.nextLine().trim();

                String encryptedText = readFile(encryptedFile);
                if (encryptedText != null) {
                    String decryptedText = decryptMessage(encryptedText, key);
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
    }
}
