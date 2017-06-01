
import data.Database;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetOTP extends HttpServlet {

    private static final String DB_LOCATION = "localhost";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String email = request.getParameter("email");
            String guid = request.getParameter("guid");
            Database DB = new Database(DB_LOCATION, "data", "root", "");
            String database = DB.getDB(email);
            DB.destroy();
            DB = new Database(DB_LOCATION, database, "root", "");
            String otp = DB.getOTP(guid);
            response.getWriter().print(otp.substring(0,4)+"-"+otp.substring(4,8)+"-"+otp.substring(8,12)+"-"+otp.substring(12));
            DB.destroy();
        } catch (Exception e) {
            System.out.println("Error in otp fetch !");
        }
    }

}
