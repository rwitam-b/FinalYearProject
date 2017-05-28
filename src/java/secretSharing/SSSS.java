package secretSharing;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class SSSS {

    private final static int CERTAINTY = 256;
    private int totalShares;
    private int thresholdShares;
    private BigInteger prime;
    private BigInteger secret;
    private final SecureRandom random = new SecureRandom();
    public SecretShare[] shares;

    public SSSS(String secret, int total, int threshold) {
        totalShares = total;
        thresholdShares = threshold;

        // Generating numeric secret from hex string
        this.secret = new BigInteger(secret, 16);

        // Generating prime number larger than the secret number
        this.prime = new BigInteger(this.secret.bitLength() + 1, CERTAINTY, random);
    }

    public SSSS(SecretShare[] shares, BigInteger prime) {
        this.prime = prime;
        this.shares = shares;
    }

    public void split() {
        final BigInteger[] coeff = new BigInteger[thresholdShares];
        coeff[0] = secret;
        for (int i = 1; i < thresholdShares; i++) {
            BigInteger r;
            while (true) {
                r = new BigInteger(prime.bitLength(), random);
                if (r.compareTo(BigInteger.ZERO) > 0 && r.compareTo(prime) < 0) {
                    break;
                }
            }
            coeff[i] = r;
        }
        final SecretShare[] shares = new SecretShare[totalShares];
        for (int x = 1; x <= totalShares; x++) {
            BigInteger accum = secret;
            for (int exp = 1; exp < thresholdShares; exp++) {
                accum = accum.add(coeff[exp].multiply(BigInteger.valueOf(x).pow(exp).mod(prime))).mod(prime);
            }
            shares[x - 1] = new SecretShare(x, accum);            
        }
        this.shares = shares;
    }

    public void combine() {
        BigInteger accum = BigInteger.ZERO;

        for (int formula = 0; formula < shares.length; formula++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int count = 0; count < shares.length; count++) {
                if (formula == count) {
                    continue;
                }
                int startposition = shares[formula].getNumber();
                int nextposition = shares[count].getNumber();

                numerator = numerator.multiply(BigInteger.valueOf(nextposition).negate()).mod(prime);
                denominator = denominator.multiply(BigInteger.valueOf(startposition - nextposition)).mod(prime);
            }
            BigInteger value = shares[formula].getShare();
            BigInteger tmp = value.multiply(numerator).multiply(modInverse(denominator, prime));
            accum = prime.add(accum).add(tmp).mod(prime);
        }
        this.secret = accum;
    }

    private static BigInteger[] gcdD(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) == 0) {
            return new BigInteger[]{a, BigInteger.ONE, BigInteger.ZERO};
        } else {
            BigInteger n = a.divide(b);
            BigInteger c = a.mod(b);
            BigInteger[] r = gcdD(b, c);
            return new BigInteger[]{r[0], r[2], r[1].subtract(r[2].multiply(n))};
        }
    }

    private static BigInteger modInverse(BigInteger k, BigInteger prime) {
        k = k.mod(prime);
        BigInteger r = (k.compareTo(BigInteger.ZERO) == -1) ? (gcdD(prime, k.negate())[2]).negate() : gcdD(prime, k)[2];
        return prime.add(r).mod(prime);
    }
    
    public String getSecret(){
        return this.secret.toString(16).toUpperCase();
    }
    
    public SecretShare[] getShares(){
        return this.shares;
    }
    
    public BigInteger getPrime(){
        return this.prime;
    }
    
    public void printShares(){
        System.out.println("Shares ->");
        for(SecretShare a:shares){
            System.out.println(a);
        }
    }
}
