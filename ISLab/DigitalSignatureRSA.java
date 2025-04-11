import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.Scanner;
import javax.crypto.Cipher;

public class DigitalSignatureRSA {

    private static final String PUBLIC_KEY_FILE = "publickey.txt";
    private static final String PRIVATE_KEY_FILE = "privatekey.txt";

    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter input message file name (e.g., message.txt): ");
            String inputFile = scanner.nextLine();
            
            System.out.print("Enter signature output file name (e.g., signature.txt): ");
            String signatureFile = scanner.nextLine();

            
            KeyPair keyPair = generateRSAKeyPair();
            

            saveKeyToFile(keyPair.getPublic(), PUBLIC_KEY_FILE);
            saveKeyToFile(keyPair.getPrivate(), PRIVATE_KEY_FILE);

            String message = readMessageFromFile(inputFile);
            System.out.println("Original Message: " + message);


            byte[] messageHash = sha256(message);
            System.out.println("SHA-256 Hash: " + bytesToHex(messageHash));

            byte[] signature = generateDigitalSignature(messageHash, keyPair.getPrivate());
            saveSignatureToFile(signature, signatureFile);
            System.out.println("Signature generated and saved to " + signatureFile);

   
            boolean isVerified = verifyDigitalSignature(messageHash, signature, keyPair.getPublic());
            System.out.println("Signature Verification: " + (isVerified ? "Valid" : "Invalid"));

            scanner.close();

        } catch (Exception e) {
            System.out.println("Error"+e);
        }
        
    }


    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }


    private static byte[] sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes());
    }

    private static byte[] generateDigitalSignature(byte[] messageHash, PrivateKey privateKey) 
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(messageHash);
    }

    private static boolean verifyDigitalSignature(byte[] messageHash, byte[] signature, PublicKey publicKey) 
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedHash = cipher.doFinal(signature);

        return MessageDigest.isEqual(messageHash, decryptedHash);
    }


    private static String readMessageFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }


    private static void saveSignatureToFile(byte[] signature, String fileName) throws IOException {
        Files.write(Paths.get(fileName), signature);
    }

    private static void saveKeyToFile(Key key, String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(fileName))) {
            oos.writeObject(key);
        }
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}