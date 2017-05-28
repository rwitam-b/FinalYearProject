package crypto;

import java.security.MessageDigest;

public class SHA256 {

    private String sha_checksum;

    public SHA256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte byteData[] = md.digest();
            this.sha_checksum = byteToHex(byteData).toUpperCase();
        } catch (Exception e) {
            System.out.println("Error Occured During SHA Checksum Calculation !");
        }
    }

    private String byteToHex(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(Integer.toString((arr[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public String getChecksum() {
        return this.sha_checksum;
    }
}
