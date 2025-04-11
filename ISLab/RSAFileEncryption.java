import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RSAFileEncryption {
    private BigInteger p, q, n, phi, e, d;
    private int bitLength = 1024; 
    private Random random;

    
    public RSAFileEncryption() {
        random = new SecureRandom();
        generateKeys();
    }

    private void generateKeys() {
    
        p = BigInteger.probablePrime(bitLength / 2, random);
        q = BigInteger.probablePrime(bitLength / 2, random);
        
       
        n = p.multiply(q);
        
      
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
  
        e = BigInteger.valueOf(65537);
        while (phi.gcd(e).intValue() > 1 && e.compareTo(phi) < 0) {
            e = e.add(BigInteger.TWO);
        }
        

        d = e.modInverse(phi);
    }

    public BigInteger[] encrypt(String message) {
        byte[] bytes = message.getBytes();
        BigInteger[] encrypted = new BigInteger[bytes.length];
        
        for (int i = 0; i < bytes.length; i++) {
            encrypted[i] = new BigInteger(String.valueOf(bytes[i])).modPow(e, n);
        }
        return encrypted;
    }


    public String decrypt(BigInteger[] encrypted) {
        byte[] bytes = new byte[encrypted.length];
        
        for (int i = 0; i < encrypted.length; i++) {
            bytes[i] = encrypted[i].modPow(d, n).byteValue();
        }
        return new String(bytes);
    }

    private void writeToFile(String filename, BigInteger[] data) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        }
    }


    private BigInteger[] readFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (BigInteger[]) ois.readObject();
        }
    }

    public static void main(String[] args) {
        try {
            RSAFileEncryption rsa = new RSAFileEncryption();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


            System.out.print("Enter input file name (with extension, e.g., input.txt): ");
            String inputFile = reader.readLine();


            StringBuilder message = new StringBuilder();
            try (BufferedReader fileReader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    message.append(line).append("\n");
                }
            }

            if (message.length() == 0) {
                System.out.println("Error: Input file is empty!");
                return;
            }

        
            BigInteger[] encryptedData = rsa.encrypt(message.toString());
            String encryptedFile = "encrypted_" + inputFile;
            rsa.writeToFile(encryptedFile, encryptedData);
            System.out.println("Message encrypted and saved to: " + encryptedFile);

           
            BigInteger[] readEncrypted = rsa.readFromFile(encryptedFile);
            String decryptedMessage = rsa.decrypt(readEncrypted);
            String decryptedFile = "decrypted_" + inputFile;
            
     
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(decryptedFile))) {
                writer.write(decryptedMessage);
            }

            System.out.println("Message decrypted and saved to: " + decryptedFile);
            System.out.println("\nOriginal message: " + message);
            System.out.println("Decrypted message: " + decryptedMessage);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}