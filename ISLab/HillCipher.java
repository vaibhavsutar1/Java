import java.io.*;
import java.util.Scanner;

public class HillCipher {

    
    public static String readFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
            return "";
        }
        return content.toString();
    }

    public static void writeFile(String filename, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(data);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
        }
    }

    public static int[][] getKeyMatrix(int n, Scanner scanner) {
        int[][] keyMatrix = new int[n][n];
        System.out.println("Enter the " + n + "×" + n + " key matrix row-wise:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                keyMatrix[i][j] = scanner.nextInt();
            }
        }
        return keyMatrix;
    }

    public static String prepareMessage(String message, int n) {
        message = message.replaceAll("\\s", "").toUpperCase();
        while (message.length() % n != 0) {
            message += "X";
        }
        return message;
    }

    public static int[][] textToVector(String text, int n) {
        int[][] vector = new int[n][1];
        for (int i = 0; i < n; i++) {
            vector[i][0] = text.charAt(i) - 'A';
        }
        return vector;
    }

    public static String vectorToText(int[][] matrix) {
        StringBuilder text = new StringBuilder();
        for(int i = 0; i < matrix.length; i++) {
            text.append((char) (matrix[i][0] + 'A'));
        }
        return text.toString();
    }

    public static int[][] matrixMultiply(int[][] key, int[][] vector, int mod) {
        int n = key.length;
        int[][] result = new int[n][1];
        for (int i = 0; i < n; i++) {
            result[i][0] = 0;
            for (int j = 0; j < n; j++) {
                result[i][0] += key[i][j] * vector[j][0];
            }
            result[i][0] %= mod;
        }
        return result;
    }

    public static String encryptMessage(String message, int[][] keyMatrix, int n) {
        String preparedMessage = prepareMessage(message, n);
        StringBuilder cipherText = new StringBuilder();
        for (int i = 0; i < preparedMessage.length(); i += n) {
            int[][] messageVector = textToVector(preparedMessage.substring(i, i + n), n);
            int[][] cipherMatrix = matrixMultiply(keyMatrix, messageVector, 26);
            cipherText.append(vectorToText(cipherMatrix));
        }
        return cipherText.toString();
    }

    public static int modInverse(int det, int mod) {
        det = det % mod;
        for (int i = 1; i < mod; i++) {
            if ((det * i) % mod == 1)
                return i;
        }
        return -1; 
    }

    public static int[][] inverseMatrix(int[][] matrix, int mod) {
        int n = matrix.length;
        int det = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) % mod;
        if (det < 0)
            det += mod;
        int detInv = modInverse(det, mod);
        if (detInv == -1)
            throw new IllegalArgumentException("Matrix is not invertible!");

        int[][] inverse = new int[n][n];
        inverse[0][0] = matrix[1][1];
        inverse[1][1] = matrix[0][0];
        inverse[0][1] = -matrix[0][1];
        inverse[1][0] = -matrix[1][0];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = ((inverse[i][j] * detInv) % mod + mod) % mod;
            }
        }
        return inverse;
    }

    public static String decryptMessage(String ciphertext, int[][] keyMatrix, int n) {
        int[][] inverseKey = inverseMatrix(keyMatrix, 26);
        StringBuilder messageText = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += n) {
            int[][] cipherVector = textToVector(ciphertext.substring(i, i + n), n);
            int[][] messageMatrix = matrixMultiply(inverseKey, cipherVector, 26);
            messageText.append(vectorToText(messageMatrix));
        }
        return messageText.toString().replace("X", ""); // Remove padding
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nHill Cipher Program");
            System.out.println("1. Encrypt Text from file");
            System.out.println("2. Decrypt Text from file");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if(choice.equals("1")) {
                System.out.print("Enter key matrix size (2 for 2×2, 3 for 3×3, etc.): ");
                int n = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter message file name: ");
                String messageFile = scanner.nextLine().trim();
                System.out.print("Enter output encrypted file name: ");
                String encryptedFile = scanner.nextLine().trim();

                String message = readFile(messageFile);
                if (message.isEmpty()) continue;

                int[][] keyMatrix = getKeyMatrix(n, scanner);

                String encryptedText = encryptMessage(message, keyMatrix, n);
                writeFile(encryptedFile, encryptedText);
                System.out.println("Encryption completed! Check " + encryptedFile);

            } else if (choice.equals("2")) {
                System.out.print("Enter key matrix size used for encryption: ");
                int n = scanner.nextInt();
                scanner.nextLine(); 
                System.out.print("Enter encrypted file name: ");
                String encryptedFile = scanner.nextLine().trim();
                System.out.print("Enter output decrypted file name: ");
                String decryptedFile = scanner.nextLine().trim();

                String ciphertext = readFile(encryptedFile);
                if (ciphertext.isEmpty()) continue;

                int[][] keyMatrix = getKeyMatrix(n, scanner);
                try {
                    String decryptedText = decryptMessage(ciphertext, keyMatrix, n);
                    writeFile(decryptedFile, decryptedText);
                    System.out.println("Decryption completed! Check " + decryptedFile);
                } catch (IllegalArgumentException e) {
                    System.out.println("Decryption failed: " + e.getMessage());
                }

            } else if (choice.equals("3")) {
                System.out.println("Exiting program. Goodbye! ");
                break;
            } else {
                System.out.println("Invalid choice! Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }
}

