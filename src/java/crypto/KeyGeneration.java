package crypto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class KeyGeneration {

    public static String getKey(String company, String plan, String fname, String lname, String phone, String address) {
        ArrayList<String> temp = new ArrayList();
        temp.add(company);
        temp.add(plan);
        temp.add(fname);
        temp.add(lname);
        temp.add(phone);
        temp.add(address);
        Collections.shuffle(temp, new SecureRandom());
        StringBuffer temp2 = new StringBuffer();
        for (String a : temp) {
            temp2.append(a.toLowerCase());
        }
        return new SHA256(temp2.toString()).getChecksum();
    }
}
