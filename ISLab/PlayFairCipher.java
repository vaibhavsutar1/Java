import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class PlayFairCipher {
    private char[][] keyTable;

    public PlayFairCipher(String key) {
        keyTable = generateKeyTable(key);
    }

    private char[][] generateKeyTable(String key) {
        key = key.toUpperCase().replaceAll("[J]", "I");
        boolean[] charUsed = new boolean[26];
        char[][] table = new char[5][5];
        StringBuilder keyBuilder = new StringBuilder();

        for (char c : key.toCharArray()) {
            if (c >= 'A' && c <= 'Z' && !charUsed[c - 'A']) {
                keyBuilder.append(c);
                charUsed[c - 'A'] = true;
            }
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J' && !charUsed[c - 'A']) {
                keyBuilder.append(c);
            }
        }

        int index = 0;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                table[row][col] = keyBuilder.charAt(index++);
            }
        }
        return table;
    }

    private String prepareText(String text) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
        StringBuilder preparedText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            preparedText.append(text.charAt(i));
            if (i < text.length() - 1 && text.charAt(i) == text.charAt(i + 1)) {
                preparedText.append("X");
            }
        }

        if (preparedText.length() % 2 != 0) {
            preparedText.append("X");
        }

        return preparedText.toString();
    }

    private String processPairs(String text, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        if (encrypt) {
            text = prepareText(text);
        }

        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);
            int[] posA = findPosition(a);
            int[] posB = findPosition(b);

            if (posA[0] == posB[0]) {
                result.append(keyTable[posA[0]][(posA[1] + (encrypt ? 1 : 4)) % 5]);
                result.append(keyTable[posB[0]][(posB[1] + (encrypt ? 1 : 4)) % 5]);
            } else if (posA[1] == posB[1]) {
                result.append(keyTable[(posA[0] + (encrypt ? 1 : 4)) % 5][posA[1]]);
                result.append(keyTable[(posB[0] + (encrypt ? 1 : 4)) % 5][posB[1]]);
            } else {
                result.append(keyTable[posA[0]][posB[1]]);
                result.append(keyTable[posB[0]][posA[1]]);
            }
        }
        return result.toString();
    }

    public String encrypt(String plaintext) {
        return processPairs(plaintext, true);
    }

    public String decrypt(String ciphertext) {
        return processPairs(ciphertext, false);
    }

    public void printKeyTable() {
        System.out.println("Playfair Cipher Key Table:");
        for (char[] row : keyTable) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    private int[] findPosition(char c) {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (keyTable[row][col] == c) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public static void writeFile(String filename, String content) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            System.out.println("File written successfully: " + filename);
        } catch (Exception e) {
            System.out.println("An error occurred while writing the file.");
            e.printStackTrace();
        }
    }

    public static String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (FileReader reader = new FileReader(fileName);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
        return content.toString().trim();
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the key: ");
        String key = scan.nextLine();

        PlayFairCipher playfair = new PlayFairCipher(key);
        playfair.printKeyTable();

        System.out.print("Enter the input file name: ");
        String inputFileName = scan.nextLine();
        System.out.print("Enter the Encrypted file name: ");
        String encryptedFile = scan.nextLine();
        System.out.print("Enter the Decrypted file name: ");
        String decryptedFile = scan.nextLine();

        String plainText = readFile(inputFileName);
        System.out.println("Original Message: " + plainText);

        String encryptedText = playfair.encrypt(plainText);
        System.out.println("Encrypted Message: " + encryptedText);
        writeFile(encryptedFile, encryptedText);

        String decryptedText = playfair.decrypt(encryptedText);
        System.out.println("Decrypted Message: " + decryptedText);
        writeFile(decryptedFile, decryptedText);
    }
}
