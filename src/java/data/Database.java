package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
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

    public void createTable(String table) {
        try {
            S = C.createStatement();
            String sql = "";
            if (table.equalsIgnoreCase("client_details")) {
                sql = "CREATE TABLE client_details(company_name VARCHAR(50),plan VARCHAR(10),fname VARCHAR(20),lname VARCHAR(20),phone VARCHAR(10),address VARCHAR(100),key_meta VARCHAR(150))";
            } else if (table.equalsIgnoreCase("participants")) {
                sql = "CREATE TABLE participants(fname VARCHAR(20),lname VARCHAR(20),guid VARCHAR(20),share VARCHAR(100))";
            }
            S.executeUpdate(sql);
            System.out.println("Table " + table + " created successfully in database " + DB_URL.substring(DB_URL.lastIndexOf("/") + 1));
        } catch (Exception e) {
            System.out.println("Error Creating Table " + table + " in database " + DB_URL.substring(DB_URL.lastIndexOf("/") + 1));
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
                PS = C.prepareStatement("INSERT INTO participants VALUES(?,?,?,?)");
                PS.setString(1, fname[a]);
                PS.setString(2, lname[a]);
                PS.setString(3, guid[a]);
                PS.setString(4, share[a].toString());
                recordsUpdated += PS.executeUpdate();
            }
            System.out.println("Successfully populated " + recordsUpdated + " participants in database !");
        } catch (Exception e) {
            System.out.println("Error Entering Values Into participants table !");
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

    public void destroy() {
        try {
            C.close();
            System.out.println("Database Connection Closed Successfully !");
        } catch (Exception e) {
            System.out.println("Error Closing Database Connection !");
        }
    }
}
