
import crypto.SHA256;
import data.Database;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import secretSharing.SSSS;
import secretSharing.SecretShare;

public class AccessCheck extends HttpServlet {

    private static final String DB_LOCATION = "localhost";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String email = request.getParameter("email");
            Database DB = new Database(DB_LOCATION, "data", "root", "");
            String database = DB.getDB(email);
            DB.destroy();
            DB = new Database(DB_LOCATION, database, "root", "");
            String key = DB.getKeyMeta();
            String prime = key.substring(64);
            key = key.substring(0, 64);
            SecretShare[] shares = DB.getOnlineShares();
            ArrayList<String> names = DB.getOnlineNames();
            SSSS shareBuilder = new SSSS(shares, new BigInteger(prime,16));
            System.out.println("Shares Built");
            shareBuilder.combine();            
            String secret = shareBuilder.getSecret();
            PrintWriter out = response.getWriter();
            if (new SHA256(secret).getChecksum().equals(key)) {
                out.println("Access Granted");
            } else {
                out.println("Access Denied");
            }
            for (String name : names) {
                out.println(name);
            }
            DB.destroy();
        } catch (Exception e) {
            System.out.println("Error in access check !");       
//            e.printStackTrace();
        }
    }

}
