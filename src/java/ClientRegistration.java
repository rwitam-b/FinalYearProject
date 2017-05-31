
import crypto.KeyGeneration;
import crypto.SHA256;
import data.Database;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import secretSharing.SSSS;

public class ClientRegistration extends HttpServlet {

    private static final String DB_LOCATION = "localhost";

    private String[] getParameterValueArray(HttpServletRequest R, String param) throws NullPointerException {
        String value[] = R.getParameterValues(param);
        if (value == null || value.length == 0) {
            throw new NullPointerException();
        } else {
            for (int a = 0; a < value.length; a++) {
                value[a] = value[a].trim();
            }
            return value;
        }
    }

    private String getParameterValue(HttpServletRequest R, String param) throws NullPointerException {
        String value = R.getParameter(param);
        if (value == null || value.isEmpty()) {
            throw new NullPointerException();
        } else {
            return value.trim();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Bad GET Request in ClientRegistration");
        response.getWriter().println("invalid request");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            System.out.println("Incoming Sign-Up Request From " + request.getRemoteAddr());
            String company = getParameterValue(request, "company");
            String email = getParameterValue(request, "email").toLowerCase();
            String password = getParameterValue(request, "pass");
            String passwordRetype = getParameterValue(request, "cpass");
            String plan = getParameterValue(request, "plan");
            String fname = getParameterValue(request, "fname");
            String lname = getParameterValue(request, "lname");
            String phone = getParameterValue(request, "phone");
            String address = getParameterValue(request, "address");
            int thresholdAccess = Integer.parseInt(getParameterValue(request, "threshold_emp"));
            String participantFname[] = getParameterValueArray(request, "emp_fname");
            String participantLname[] = getParameterValueArray(request, "emp_lname");
            String participantId[] = getParameterValueArray(request, "emp_id");
            System.out.println("Successfully Read All Form Parameters");
            if (!password.equals(passwordRetype)) {
                response.getWriter().println("Password Does Not Match !");
                System.out.println("Passwords Don't Match !");
                System.out.println("Ending Process...");
            } else {
                // Put details in login table
                Database DB = new Database(DB_LOCATION, "data", "root", "");
                DB.createLogin(email, new SHA256(password).getChecksum(), new SHA256(company).getChecksum().substring(0, 32));
                DB.destroy();

                // Create new company database
                DB = new Database(DB_LOCATION, "root", "");
                String db_name = new SHA256(company).getChecksum().substring(0, 32);
                DB.createDatabase(db_name);
                DB.destroy();

                // Generating Encryption Key
                String AES_Key = KeyGeneration.getKey(company, plan, fname, lname, phone, address);
                System.out.println("Encryption Key Generated : "+AES_Key);

                // Generating Key Shares/Parts
                SSSS sharingScheme = new SSSS(AES_Key, participantId.length, thresholdAccess);
                sharingScheme.split();
                System.out.println("Key Splitting Done With "+participantFname.length+"-"+thresholdAccess+" scheme.");
                sharingScheme.printShares();

                // Create tables in company database
                DB = new Database(DB_LOCATION, db_name, "root", "");
                DB.createTables();
                String key_meta = new SHA256(AES_Key).getChecksum() + sharingScheme.getPrime().toString(16);
                DB.enterClientDetails(company, plan, fname, lname, phone, address, key_meta);
                DB.enterParticipantDetails(participantFname, participantLname, participantId, sharingScheme.getShares());
            }
            response.getWriter().println("Successfully Completed !");
//            response.sendRedirect("login.html");
        } catch (NullPointerException e) {
            response.getWriter().println("You Have To Fill In All The Fields !");
        } catch (Exception e) {
            response.getWriter().println("Error In Registration ! Please Try Again After A While !");
        }
    }

}
