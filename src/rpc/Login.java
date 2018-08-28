package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
        try {
            JSONObject obj = new JSONObject();
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(403);
                obj.put("status", "Session invalid");
                System.out.println("failure-get-login");
            } else {
            		response.setStatus(200);
                String username = (String) session.getAttribute("username");
                	String vip = (String) session.getAttribute("vip");
                obj.put("status", "OK");
                obj.put("username", username);
                	obj.put("vip", vip);
                	System.out.println("success-get-login");
            }
            RpcHelper.writeJsonObject(response, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
        try {
            JSONObject input = RpcHelper.readJSONObject(request);
            String username = input.getString("username");
            String pwd = input.getString("password");
            
            JSONObject obj = new JSONObject();
            	System.out.println("try to post login");
            if (conn.verifyLogin(username, pwd)) {
                HttpSession session = request.getSession();
                
                String vip = conn.getUserVip(username);
                session.setAttribute("username", username);
                session.setAttribute("vip", vip);
                // Set session to expire in 10 minutes.
                session.setMaxInactiveInterval(10 * 60);
                // Get user name
                obj.put("status", "OK");
                obj.put("username", username);
                obj.put("vip", vip);
                
                System.out.println("success-post-login");
            } else {
                System.out.println("failure-post-login");
                response.setStatus(401);
            }
            RpcHelper.writeJsonObject(response, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
	}

}
