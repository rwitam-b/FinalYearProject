
import data.Database;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInvalidate extends HttpServlet {

    private static final String DB_LOCATION = "localhost";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("invalid request");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String guid = request.getParameter("guid");
        String email = request.getParameter("email");
        try {
            System.out.println("Invalidation request received for " + email + " GUID@" + guid);
            Database DB = new Database(DB_LOCATION, "data", "root", "");
            String database = DB.getDB(email);
            DB.destroy();
            DB = new Database(DB_LOCATION, database, "root", "");
            DB.changeParticipantStatus(guid, "N");
            DB.destroy();
            System.out.println(email + " GUID@" + guid + " successfully invalidated");
        } catch (Exception e) {
            System.out.println("Error invalidating " + guid + " at the ClientDetail Server !");
        }
    }
}
