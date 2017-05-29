
import crypto.SHA256;
import data.Database;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClientLogin extends HttpServlet {

    private static final String DB_LOCATION = "localhost";

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
        System.out.println("Bad GET Request in ClientLogin");
        response.getWriter().println("invalid request");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            System.out.println("Incoming Sign-In Request From " + request.getRemoteAddr());
            String email = getParameterValue(request, "login_email").toLowerCase();
            String password = getParameterValue(request, "login_pwd");
            password = new SHA256(password).getChecksum();
            System.out.println("Successfully Read All Form Parameters");
            Database DB = new Database(DB_LOCATION, "data", "root", "");
            if (DB.checkLogin(email, password)) {
                System.out.println("Logged in successfully @" + email);
                DB.destroy();
                DB = new Database(DB_LOCATION, "data", "root", "");
                String db = DB.getDB(email);
                response.getWriter().println(" Login Successful !");
            } else {
                System.out.println("Invalid Login Credentials for " + email);
                response.getWriter().println("Invalid Login Credentials !");
            }
            DB.destroy();
        } catch (NullPointerException e) {
            response.getWriter().println("You Have To Fill In All The Fields !");
        } catch (Exception e) {
            response.getWriter().println("Error In Login ! Please Try Again After A While !");
        }
    }

}
