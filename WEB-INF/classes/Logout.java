/*
 * Story Reader V2: logout
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to handle user logout request
 * */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class Logout extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			HttpSession session = request.getSession();
			
			//check user has login
			if(session.getAttribute("user") != null) {
				Log log = Log.getInstance();
				// log user logout request
				log.write("Server", "Logout successfully (Goodbye, "+session.getAttribute("user")+")", request);
			}
			
			//remove all session attribute
			session.removeAttribute("user");
			session.removeAttribute("bid");
			
			//generate page to let user know that he has logout
			pw.println("<!DOCTYPE html><!-- Story Reader V2 by Mingchao Liao --><html><head><title>Error</title></head><body>"
					+ "<h1>You have successfully logout</h1>"
					+ "<a href='login.html'><h3>Click here to login again</h3></a>"
					+ "</body></html>");
		} catch (Exception e) {
			Log.getInstance().write("Server Error", e.getMessage(), request);
			response.sendRedirect(response.encodeRedirectURL("error.html"));
		}
	}

}
