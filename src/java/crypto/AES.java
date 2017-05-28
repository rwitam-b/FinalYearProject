package crypto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static final int DECRYPT_MODE = 1;
    public static final int ENCRYPT_MODE = 2;
    private int mode;
    private SecuredHeader header;
    private String key;
    private byte[] salt;
    private byte[] IV;
    private FileInputStream IF;
    private FileOutputStream OF;
    private Cipher cipher;

    AES(int opmode, String filename, String key) {
        try {
            this.mode = opmode;
            this.key = key;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            switch (opmode) {
                case DECRYPT_MODE:
                    try {
                        byte[] headerBytes = new byte[48];

                        // Check for proper file extension
                        if (!filename.endsWith(".WCRY")) {
                            throw new Exception();
                        }

                        // Set input and output files
                        this.IF = new FileInputStream(filename);
                        this.OF = new FileOutputStream(filename.substring(0, filename.length() - 5));

                        // Reading file header
                        IF.read(headerBytes);

                        // Extracting metadata from header
                        this.header = new SecuredHeader(headerBytes);
                        this.salt = this.header.getSalt();
                        this.IV = this.header.getIV();

                        // Generating encryption key from user-supplied key
                        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                        KeySpec keySpec = new PBEKeySpec(key.toCharArray(), this.salt, 65536, 256);
                        SecretKey secretKey = factory.generateSecret(keySpec);
                        SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

                        // Initializing Cipher
                        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(this.IV));

                    } catch (FileNotFoundException e) {
                        System.out.println("Input File Does Not Exist !");
                    } catch (Exception e) {
                        System.out.println("Bad Encrypted File Format !");
                    }
                    break;
                case ENCRYPT_MODE:
                    try {
                        // Set input and output files
                        this.IF = new FileInputStream(filename);
                        this.OF = new FileOutputStream(filename + ".WCRY");

                        // Initializing salt
                        this.salt = new byte[8];
                        SecureRandom secureRandom = new SecureRandom();
                        secureRandom.nextBytes(this.salt);

                        // Generating encryption key from user-supplied key
                        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                        KeySpec keySpec = new PBEKeySpec(key.toCharArray(), salt, 65536, 256);
                        SecretKey secretKey = factory.generateSecret(keySpec);
                        SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
                        System.out.println("Secret : "+Arrays.toString(secret.getEncoded()));
                        System.out.println("Secret Length : "+(secret.getEncoded()).length);

                        // Initializing cipher                        
                        cipher.init(Cipher.ENCRYPT_MODE, secret);

                        // Capture IV
                        this.IV = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();

                        // Generate SecureHeader for secure disk storage of IV and salt
                        this.header = new SecuredHeader(this.IV, this.salt);

                    } catch (FileNotFoundException e) {
                        System.out.println("Input File Does Not Exist !");
                    } catch (Exception e) {
                        System.out.println("AES Initialization Could Not Be Completed !");
                    }
                    break;
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Bad AES Init Parameter");
        }
    }

    public void encrypt() {
        try {
            // Writing file header containing obfuscated IV & Salt
            OF.write(header.getHeader());

            // AES Encrypted File Writing
            byte[] input = new byte[64];
            int bytesRead;
            while ((bytesRead = IF.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null) {
                    OF.write(output);
                }
            }
            byte[] output = cipher.doFinal();
            if (output != null) {
                OF.write(output);
            }
            IF.close();
            OF.flush();
            OF.close();
            System.out.println("File Encrypted Successfully !");
        } catch (Exception e) {
            System.out.println("Encryption Failure !");
        }
    }

    public void decrypt() {
        try {
            // Decrypting the encrypted file
            byte[] in = new byte[64];
            int read;
            while ((read = IF.read(in)) != -1) {
                byte[] output = cipher.update(in, 0, read);
                if (output != null) {
                    OF.write(output);
                }
            }
            byte[] output = cipher.doFinal();
            if (output != null) {
                OF.write(output);
            }
            IF.close();
            OF.flush();
            OF.close();
            System.out.println("File Decrypted Successfully !");
        } catch (BadPaddingException e) {
            System.out.println("Wrong Encryption Key !");
        } catch (Exception e) {
            System.out.println("Decryption Failed !");
        }
    }
}
