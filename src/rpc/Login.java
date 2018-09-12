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
 * Servlet implementation class LogIn
 */
@WebServlet(name = "login", urlPatterns = { "/login" })
public class Login extends HttpServlet {
	private static final String USER_ID = "user_id";
	private static final String STATUS = "status";
	private static final String USERNAME = "username";
	private static final String AUTHORIZED = "authorized";
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
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		
		DBConnection conn = DBConnectionFactory.getConnection();
		//String userId = request.getParameter("user_id");
		JSONObject obj = new JSONObject();
		try {
			if (request.getSession(false) == null) {
				response.setStatus(403);
				obj.put(AUTHORIZED, false);
			}else {
				String userIdSession = session.getAttribute("user_id").toString();
				String userName = conn.getFullname(userIdSession);
				obj.put(AUTHORIZED, true);
				obj.put(USERNAME, userName);
				obj.put(STATUS, "OK");
				obj.put(USER_ID, userIdSession);
			}
			RpcHelper.writeJSONObj(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString(USER_ID);
			String pwd = input.getString("password");
			DBConnection conn = DBConnectionFactory.getConnection();
			JSONObject obj = new JSONObject();

			if (conn.verifyLogin(userId, pwd)) {
				HttpSession sess = request.getSession();
				sess.setAttribute(USER_ID, userId);
				sess.setMaxInactiveInterval(10 * 60);
				String userName = conn.getFullname(userId);
				System.out.println(userName);
				obj.put(AUTHORIZED, true);
				obj.put(USERNAME, userName);
				obj.put(STATUS, "OK");
				obj.put(USER_ID, userId);
				// response.sendRedirect("home.html");
				
			}else {
				obj.put(AUTHORIZED, false);
				obj.put(STATUS, "fail");
			}
			RpcHelper.writeJSONObj(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
