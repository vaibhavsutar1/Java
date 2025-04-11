import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class HashFunctions {

    public static String computeFileHash(String InputFileName, String hashAlgorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
        try (FileInputStream inputStream = new FileInputStream(InputFileName)) {
            byte[] buffer = new byte[8192];
            int byteRead;
            while ((byteRead = inputStream.read(buffer)) != -1) { 
                messageDigest.update(buffer, 0, byteRead);
            }
        }
        byte[] hashByte = messageDigest.digest();
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashByte) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }

    public static void storeHashToFile(String InputFileName, String hashAlgorithm, String OutputFileName) throws NoSuchAlgorithmException, IOException {
        String hash = computeFileHash(InputFileName, hashAlgorithm);
        try (PrintWriter writer = new PrintWriter(OutputFileName)) {
            writer.println(hash);
        }
    }

    public static void checkIntegrity(String filePath, String hashFileName, String hashAlgorithm) throws NoSuchAlgorithmException, IOException {
        String originalHash = computeFileHash(filePath, hashAlgorithm);
        String storedHash = Files.readString(Paths.get(hashFileName)).trim(); 

        System.out.println("Original File Hash: " + originalHash);
        System.out.println("Stored File Hash: " + storedHash);

        if (originalHash.equals(storedHash)) {
            System.out.println("Integrity Checked, Both Hashcodes are same");
        } else {
            System.out.println("Hash Codes are not same");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the file name: ");
        String InputFileName = scanner.nextLine();

        System.out.print("Enter the hash algorithm: ");
        String hashAlgorithm = scanner.nextLine();

        System.out.print("Enter the output file name for the hash: ");
        String OutputFileName = scanner.nextLine();

        try {
            storeHashToFile(InputFileName, hashAlgorithm, OutputFileName);
            System.out.println("Hash stored successfully to " + OutputFileName);

            System.out.print("Do you want to check the file integrity? (yes/no): ");
            String integrityCheck = scanner.nextLine().toLowerCase();

            if (integrityCheck.equals("yes")) {
                checkIntegrity(InputFileName, OutputFileName, hashAlgorithm);
            }

        } catch (NoSuchAlgorithmException | IOException e) {
            System.out.println(e);
        } finally {
            scanner.close();
        }
    }
}
