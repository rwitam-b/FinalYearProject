package data;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Hex;
import secretSharing.SecretShare;

public class Database {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private String DB_URL;
    private String USER;
    private String PASS;
    private Connection C;
    private Statement S;
    private PreparedStatement PS;

    public Database(String address, String db, String user, String password) {
        try {
            this.USER = user;
            this.PASS = password;
            this.DB_URL = "jdbc:mysql://" + address + "/" + db;
            Class.forName(JDBC_DRIVER);
            C = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Database Connectivity Established !");
        } catch (Exception e) {
            System.out.println("Database Connection Error !");
            System.out.println(e);
        }
    }

    public Database(String address, String user, String password) {
        try {
            this.USER = user;
            this.PASS = password;
            this.DB_URL = "jdbc:mysql://" + address + "/";
            Class.forName(JDBC_DRIVER);
            C = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Database Connectivity Established !");
        } catch (Exception e) {
            System.out.println("Database Connection Error !");
        }
    }

    public void createDatabase(String db_name) {
        try {
            S = C.createStatement();
            String sql = "CREATE DATABASE " + db_name;
            S.executeUpdate(sql);
            System.out.println("Database " + db_name + " created successfully !");
        } catch (Exception e) {
            System.out.println("Error Creating Database !");
            System.out.println(e);
            e.printStackTrace();
        }

    }

    public void createTables() {
        try {
            S = C.createStatement();
            String sql = "";
            sql = "CREATE TABLE client_details(company_name VARCHAR(50),plan VARCHAR(10),fname VARCHAR(20),lname VARCHAR(20),phone VARCHAR(10),address VARCHAR(100),key_meta VARCHAR(150))";
            S.executeUpdate(sql);
            sql = "CREATE TABLE participants(fname VARCHAR(20),lname VARCHAR(20),guid VARCHAR(20),share VARCHAR(100),online_status VARCHAR(1),otp VARCHAR(16))";
            S.executeUpdate(sql);
            sql = "CREATE TABLE files (name VARCHAR(100),size VARCHAR(10),uploader VARCHAR(50),guid VARCHAR(20))";
            S.executeUpdate(sql);
            System.out.println("Tables created successfully in database " + DB_URL.substring(DB_URL.lastIndexOf("/") + 1));
        } catch (Exception e) {
            System.out.println("Error Creating Tables in database " + DB_URL.substring(DB_URL.lastIndexOf("/") + 1));
        }
    }

    public void enterClientDetails(String c_name, String plan, String fname, String lname, String phone, String address, String key_meta) {
        try {
            PS = C.prepareStatement("INSERT INTO client_details VALUES(?,?,?,?,?,?,?)");
            PS.setString(1, c_name);
            PS.setString(2, plan);
            PS.setString(3, fname);
            PS.setString(4, lname);
            PS.setString(5, phone);
            PS.setString(6, address);
            PS.setString(7, key_meta);
            int recordsUpdated = PS.executeUpdate();
            System.out.println("Successfully populated " + recordsUpdated + " client_details in database !");
        } catch (Exception e) {
            System.out.println("Error Entering Values Into client_details table !");
        }
    }

    public void enterParticipantDetails(String fname[], String lname[], String guid[], SecretShare share[]) {
        try {
            int recordsUpdated = 0;
            for (int a = 0; a < share.length; a++) {
                PS = C.prepareStatement("INSERT INTO participants VALUES(?,?,?,?,?,?)");
                PS.setString(1, fname[a]);
                PS.setString(2, lname[a]);
                PS.setString(3, guid[a]);
                PS.setString(4, share[a].toString());
                PS.setString(5, "N");
                PS.setString(6, "");
                recordsUpdated += PS.executeUpdate();
            }
            System.out.println("Successfully populated " + recordsUpdated + " participants in database !");
        } catch (Exception e) {
            System.out.println("Error Entering Values Into participants table !");
        }
    }

    public void changeParticipantStatus(String guid, String status) {
        System.out.println("Attempting to change online status for " + guid);
        try {
            PS = C.prepareStatement("UPDATE participants SET online_status=? WHERE guid=?");
            PS.setString(1, status);
            PS.setString(2, guid);
            int change = PS.executeUpdate();
            if (change == 0) {
                System.out.println("User " + guid + " is not registered!");
            } else {
                System.out.println("User " + guid + " is now " + status + "!");
            }
        } catch (Exception e) {
            System.out.println("Error While Attempting To Change Online Status For " + guid);
        }
    }

    public void createLogin(String email, String password, String db_name) {
        try {
            PS = C.prepareStatement("INSERT INTO login VALUES(?,?,?)");
            PS.setString(1, email.trim().toLowerCase());
            PS.setString(2, password);
            PS.setString(3, db_name);
            int recordsUpdated = PS.executeUpdate();
            System.out.println(recordsUpdated + " records inserted");
            System.out.println("Email Added : " + email);
        } catch (Exception e) {
            System.out.println("Error Creating Login Table Entries !");
        }
    }

    public void generateOTP(String guid) {
        System.out.println("Attempting to generate OTP for " + guid);
        try {
            byte[] otpBytes = new byte[8];
            SecureRandom R = new SecureRandom();
            R.nextBytes(otpBytes);
            String otp = Hex.encodeHexString(otpBytes).toUpperCase();
            PS = C.prepareStatement("UPDATE participants SET otp=? WHERE guid=?");
            PS.setString(1, otp);
            PS.setString(2, guid);
            int change = PS.executeUpdate();
            if (change == 0) {
                System.out.println("User " + guid + " is not registered!");
            } else {
                System.out.println("OTP has been generated for GUID " + guid);
            }
        } catch (Exception e) {
            System.out.println("Error While Attempting To Change Online Status For " + guid);
        }
    }

    public String getOTP(String guid) {
        String otp = "";
        try {
            PS = C.prepareStatement("SELECT * FROM participants WHERE guid=?");
            PS.setString(1, guid);
            ResultSet RS = PS.executeQuery();
            RS.next();
            otp = RS.getString("otp");
            System.out.println("Retreived otp for " + guid + " successfully !");
        } catch (Exception e) {
            System.out.println("Error retreiving otp for " + guid);
        }
        return otp;
    }

    public String getKeyMeta() {
        String out = "";
        try {
            PS = C.prepareStatement("SELECT key_meta FROM client_details");
            ResultSet RS = PS.executeQuery();
            RS.next();
            out = RS.getString("key_meta");
            System.out.println("KeyMeta Retreived Successfully!");
        } catch (Exception e) {
            System.out.println("Error retreiving KeyMeta!");
        }
        return out;
    }

    public SecretShare[] getOnlineShares() {
        SecretShare[] shares = null;
        try {
            PS = C.prepareStatement("SELECT * FROM participants WHERE online_status=?");
            PS.setString(1, "Y");
            ResultSet RS = PS.executeQuery();
            RS.last();
            int count = RS.getRow();
            shares = new SecretShare[count];
            RS.beforeFirst();
            int shareCounter = 0;
            while (RS.next()) {
                String shareString[] = RS.getString("share").split("-");
                shares[shareCounter] = new SecretShare(Integer.parseInt(shareString[0]), new BigInteger(shareString[1]));
            }
            System.out.println("Retreived all shares for online members successfully");
        } catch (Exception e) {
            System.out.println("Error retreiving online status for participants!");
        }
        return shares;
    }

    public ArrayList<String> getOnlineNames() {
        ArrayList<String> names = new ArrayList();
        try {
            PS = C.prepareStatement("SELECT * FROM participants WHERE online_status=?");
            PS.setString(1, "Y");
            ResultSet RS = PS.executeQuery();
            while (RS.next()) {
                String temp = RS.getString("fname") + " " + RS.getString("lname");
                names.add(temp);
            }
            System.out.println("Retreived all names for online members successfully");
        } catch (Exception e) {
            System.out.println("Error retreiving online status for participants!");
        }
        return names;
    }

    public boolean verifyOTP(String guid, String otp) {
        boolean out = false;
        System.out.println("Attempting to verify OTP for " + guid);
        try {
            PS = C.prepareStatement("SELECT otp FROM participants WHERE guid=?");
            PS.setString(1, guid);
            ResultSet RS = PS.executeQuery();
            if (!RS.isBeforeFirst()) {
                System.out.println("User " + guid + " is not registered!");
            } else {
                RS.next();
                String dbOTP = RS.getString("otp");
                System.out.println("OTP has been retreived from DB for GUID " + guid);
                if (dbOTP.length() == 0) {
                    System.out.println("User GUID@" + guid + " does not have a generated OTP at the moment !");
                } else if (dbOTP.equals(otp)) {
                    System.out.println("OTP verified! Redirecting to Cryptex");
                    PS = C.prepareStatement("UPDATE participants SET otp=? WHERE guid=?");
                    PS.setString(1, "");
                    PS.setString(2, guid);
                    int change = PS.executeUpdate();
                    System.out.println("OTP cleared from DB for GUID " + guid);
                    out = true;
                } else {
                    System.out.println("OTP doesn't match the generated OTP !");
                }
            }
        } catch (Exception e) {
            System.out.println("Error While Attempting To Verify OTP For " + guid);
        }
        return out;
    }

    public boolean checkLogin(String email, String password) {
        boolean out = false;
        try {
            PS = C.prepareStatement("SELECT password FROM login WHERE email=?");
            PS.setString(1, email);
            ResultSet RS = PS.executeQuery();
            if (!RS.isBeforeFirst()) {
                System.out.println("User " + email + " is not registered !");
            } else {
                RS.next();
                String dbPass = RS.getString("password");
                if (dbPass.equals(password)) {
                    out = true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error While Attempting To Sign-In For " + email);
        }
        return out;
    }

    public void updateFile(String file, String size, String uploader, String guid) {
        System.out.println("Attempting to update file " + file + " for user " + uploader);
        try {
            PS = C.prepareStatement("INSERT INTO files VALUES(?,?,?,?)");
            PS.setString(1, file);
            PS.setString(2, size);
            PS.setString(3, uploader);
            PS.setString(4, guid);
            int change = PS.executeUpdate();
            System.out.println("File details recorded for " + file + " @" + uploader);
        } catch (Exception e) {
            System.out.println("Error While Attempting To Record File Details");
        }
    }

    public String getDB(String email) {
        System.out.println("Attempting to retreive DB for " + email);
        String out = "";
        try {
            PS = C.prepareStatement("SELECT db FROM login WHERE email=?");
            PS.setString(1, email);
            ResultSet RS = PS.executeQuery();
            if (!RS.isBeforeFirst()) {
                System.out.println("User " + email + " is not registered !");
            } else {
                RS.next();
                out = RS.getString("db");
                System.out.println("DB " + out + " fetched successfully for " + email);
            }
        } catch (Exception e) {
            System.out.println("Error While Attempting To Retreive Database For " + email);
        }
        return out;
    }

    public void destroy() {
        try {
            C.close();
            System.out.println("Database Connection Closed Successfully !");
        } catch (Exception e) {
            System.out.println("Error Closing Database Connection !");
        }
    }
}
