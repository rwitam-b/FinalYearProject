
import data.Database;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Control extends HttpServlet {
    
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
            System.out.println("OTP request received from " + email + " GUID@" + guid);
            Database DB = new Database(DB_LOCATION, "data", "root", "");
            String database = DB.getDB(email);
            DB.destroy();
            DB = new Database(DB_LOCATION, database, "root", "");
            DB.generateOTP(guid);
            DB.destroy();
        } catch (Exception e) {
            System.out.println("Error in generating OTP at the ClientDetail Server !");
        }
    }
    
}
