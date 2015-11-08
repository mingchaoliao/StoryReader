/*
 * Story Reader V2: Control
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to init user request and generate home page
 * */


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class Controller extends HttpServlet {
	private HttpSession session;
	private HtmlHandler html;
	private FreemarkerHandler marker;
	private SQL_Handler sql;
	private PrintWriter out;
	private Log log;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get session
		session = request.getSession();
		//set response type to html
		response.setContentType("text/html");
		//set writer
		out = response.getWriter();
		
		//get freemarker handler
		marker = FreemarkerHandler.getInstance(this);
		//get html handler
		html = HtmlHandler.getInstance();
		//get sql handler
		sql = SQL_Handler.getInstance();
		//get logger
		log = Log.getInstance();
		
		try {
			processRequest(request,response);
		} catch (TemplateException e) {
			log.write("Server Error", e.getMessage(), request);
			response.sendRedirect(response.encodeRedirectURL("error.html"));
		}	
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse res) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		//write access log
		log.write("User", "Access index.html", req);
		
		//generate welcome message
		String welcome = "<h1>"+ "Welcome To Story Reader Version 2" + "<br><br>"+ "</h1>";
		
		//if user has login, generate link to redirect user to select page. if not, generate link to let user login
		String message = "";
		if(session.getAttribute("user") != null) {
			message = "<h1><a href='select.html'>Click here to select book.</a></h1>";
		} else {
			message = "<h1>Before starting reading, please login first!</h1>";
		}
		marker.put("list",welcome+message);
		marker.process(session, out);
	}
}
