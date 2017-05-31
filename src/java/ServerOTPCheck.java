
import data.Database;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerOTPCheck extends HttpServlet {

    private static final String DB_LOCATION = "localhost";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("invalid request");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String guid = request.getParameter("guid");
            String email = request.getParameter("email");
            String otp = request.getParameter("otp");
            System.out.println("OTP verification request received from " + email + " GUID@" + guid);
            Database DB = new Database(DB_LOCATION, "data", "root", "");
            String database = DB.getDB(email);
            DB.destroy();
            DB = new Database(DB_LOCATION, database, "root", "");
            if (DB.verifyOTP(guid, otp)) {
                response.getWriter().println("allow");
                DB.changeParticipantStatus(guid, "Y");
            } else {
                response.getWriter().println("block");
            }
            DB.destroy();
        } catch (Exception e) {
            System.out.println("Error occured during OTP validation !");
        }
    }

}
