package crypto;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Stack;

public class SecuredHeader implements Serializable {

    private byte[] data;
    private byte[] IV;
    private byte[] salt;

    SecuredHeader(byte[] IV, byte[] salt) {
        this.IV = IV;
        this.salt = salt;
        data = new byte[48];
        new SecureRandom().nextBytes(data);
        Stack<Byte> temp = new Stack();
        for (byte b : IV) {
            temp.push(b);
        }
        for (byte b : salt) {
            temp.push(b);
        }
        for (int a = 8; !temp.empty(); a += (a < 40) ? 16 : (a == 40) ? 7 : -2) {
            for (int b = a; b > 0; b -= (b == 41) ? 256 : (b == 25) ? 1024 : 9) {
                data[b - 1] = temp.pop();
            }
        }
    }

    SecuredHeader(byte[] data) {
        this.data = data;
        Stack<Byte> temp = new Stack();
        for (int a = 8; a != 39; a += (a < 40) ? 16 : (a == 40) ? 7 : -2) {
            for (int b = a; b > 0; b -= (b == 41) ? 256 : (b == 25) ? 1024 : 9) {
                temp.push(data[b - 1]);
            }
        }
        this.IV = new byte[16];
        this.salt = new byte[8];
        for (int a = 0; a < 16; a++) {
            this.IV[a] = temp.pop();
        }
        for (int a = 0; a < 8; a++) {
            this.salt[a] = temp.pop();
        }
    }

    public byte[] getHeader() {
        return data;
    }

    public byte[] getIV() {
        return IV;
    }

    public byte[] getSalt() {
        return salt;
    }
}
