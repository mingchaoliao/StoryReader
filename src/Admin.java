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

public class Admin extends HttpServlet {
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
		} catch (TemplateException | SQLException e) {
			log.write("Server Error", e.getMessage(), request);
			response.sendRedirect(response.encodeRedirectURL("error.html"));
		}	
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse res) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException, SQLException {
		if(session.getAttribute("user") == null || !session.getAttribute("user").equals("admin")) {
			log.write("user", "Admin Page: Access Denied", req);
			res.sendRedirect("error.html");
			return;
		}

		String menu = "<div class='list-group'>"
				+ "<a href='index.html' class='list-group-item'>Home</a>"
				+ "<a href='select.html' class='list-group-item'>Select Book</a>"
				+ "<a href='storyRust' class='list-group-item'>Rust API</a>"
				+ "</div>";
		
		marker.put("menu", menu);
		marker.process(session, out,"admin.html");
	}
}