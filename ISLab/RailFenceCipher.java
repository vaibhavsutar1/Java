import java.io.*;
import java.util.Scanner;

public class RailFenceCipher {
    public static String encrypt(String plainText, int key) {
        plainText = plainText.replaceAll(" ", "").toUpperCase();
        char[][] fence = new char[key][plainText.length()];
        int row = 0, direction = 1;
        
        for (int i = 0; i < plainText.length(); i++) {
            fence[row][i] = plainText.charAt(i);
            
            if (row == 0) direction = 1;
            else if (row == key - 1) direction = -1;
            
            row += direction;
        }
        
        StringBuilder cipherText = new StringBuilder();
        for (char[] rail : fence) {
            for (char ch : rail) {
                if (ch != '\0') cipherText.append(ch);
            }
        }
        return cipherText.toString();
    }
    
    public static String decrypt(String cipherText, int key) {
        char[][] fence = new char[key][cipherText.length()];
        int row = 0, direction = 1;

        for (int i = 0; i < cipherText.length(); i++) {
            fence[row][i] = '*';
            if (row == 0) direction = 1;
            else if (row == key - 1) direction = -1;
            row += direction;
        }
        
        int index = 0;
        for (int r = 0; r < key; r++) {
            for (int c = 0; c < cipherText.length(); c++) {
                if (fence[r][c] == '*' && index < cipherText.length()) {
                    fence[r][c] = cipherText.charAt(index++);
                }
            }
        }
        
        StringBuilder decryptedText = new StringBuilder();
        row = 0;
        direction = 1;
        for (int i = 0; i < cipherText.length(); i++) {
            decryptedText.append(fence[row][i]);
            if (row == 0) direction = 1;
            else if (row == key - 1) direction = -1;
            row += direction;
        }
        return decryptedText.toString();
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the input file name: ");
        String inputFileName = scanner.nextLine();
        
        System.out.print("Enter the key: ");
        int key = scanner.nextInt();
        scanner.nextLine(); 
        
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append(" ");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }
        
        System.out.print("1. Encryption\n2. Decryption\nEnter choice: ");
        String choice = scanner.nextLine().trim();
        
        System.out.print("Enter the output file name: ");
        String outputFileName = scanner.nextLine();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            if (choice.equals("1")) {
                writer.write(encrypt(text.toString(), key));
            } else if (choice.equals("2")) {
                writer.write(decrypt(text.toString(), key));
            } else {
                System.out.println("Wrong choice");
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}

