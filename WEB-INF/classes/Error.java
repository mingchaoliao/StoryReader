
/*
 * Story Reader V2: Error
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to handle "access denied"
 * */
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Error extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//write access denied log
			Log log = Log.getInstance();
			log.write("Server", "Access Denied", request);
			
			//generate error page:
			//   if user has login, generate page to let user go to home page
			//   if not, generate page to let user go to login page
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.println("<!DOCTYPE html><!-- Story Reader V2 by Mingchao Liao --><html><head><title>Error</title></head><body>"
					+ "<h1>Access Denied</h1>"
					+ (request.getSession().getAttribute("user") == null ? "<a href='login.html'><h1>Click here to Login</h1></a>" : "<a href='index.html'><h1>Click here to Home page</h1></a>")
					+ "</body></html>");
		} catch (Exception e) {
			Log.getInstance().write("Server Error", e.getMessage(), request);
		}
	}

}
