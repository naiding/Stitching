package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
        try {
            JSONObject input = RpcHelper.readJSONObject(request);
            String username = input.getString("username");
            String email = input.getString("email");
            String pwd = input.getString("password");
            JSONObject obj = new JSONObject();
        	if (conn.existUser(username)) {
        	    response.setStatus(403);
        		obj.put("status", "Username already exists");
			} else if (conn.createUser(username, pwd, email)) {
                HttpSession session = request.getSession();
				session.setAttribute("username", username);
                session.setAttribute("vip", "0");
                session.setMaxInactiveInterval(10 * 60);
				obj.put("status", "OK");
			} else {
			    response.setStatus(403);
				obj.put("status", "Create user failed");
			}          		
            RpcHelper.writeJsonObject(response, obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
	}
}
