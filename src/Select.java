/*
 * Story Reader V2: Select
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to get a list of book name from database to let user choose
 * */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.template.TemplateException;

public class Select extends HttpServlet {
	private HttpSession session;
	private HtmlHandler html;
	private FreemarkerHandler marker;
	private SQL_Handler sql;
	private PrintWriter out;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			//do initialization
			init(req,res);
			//handle http get request
			get(req,res);
		} catch (Exception e) {
			Log.getInstance().write("Server Error", e.getMessage(), req);
			res.sendRedirect(res.encodeRedirectURL("error.html"));
		}
	}

	private void get(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException, TemplateException {
		
		// check if user did not signin
		if(session.getAttribute("user") == null) {
			res.sendRedirect(res.encodeRedirectURL("error.html"));
			return;
		}
		
		// get a list contains all book name
		ArrayList<String[]> booklist = sql.getAllBooks();
		if(booklist == null) {
			marker.put("list","<h1>There is no books now!</h1>");
			marker.process(session,out);
			return;
		}
		
		// generate html stuff to let user select book
		String list = createList(booklist,res);
		if(session.getAttribute("error") != null) {
			if(session.getAttribute("error").equals("error")) {
				marker.put("menu", "<p style='color:red'>Please select a book</p>");
				session.setAttribute("error", "none");
			}
		}
	
		Log.getInstance().write("User", "User("+session.getAttribute("user")+") start choose book", req);
		marker.put("menu", "<div class='list-group'>"
				+ "<a href='index.html' class='list-group-item'>Home</a>"
				+ "<a href='storyRest' class='list-group-item'>Rest API</a>"
				+ "</div>");
		marker.put("list",list);
		marker.process(session,out);
	}

	// this method is user to create a html form to let user choose book
	private String createList(ArrayList<String[]> booklist, HttpServletResponse res) {
		String tem = "<form id='se' method='post' action='"+res.encodeRedirectURL("read.html")+"'>";
		tem += "<table class='table table-striped'>"
				+ "<thead>"
				+ "<tr>"
				+ "<th>Select</th>"
				+ "<th>Name</th>"
				+ "<th>Pages</th>"
				+ "<th>Author</th>"
				+ "<th>Publisher</th>"
				+ "</tr>"
				+ "</thead>"
				+ "<tbody>";
			
		for(String[] s : booklist) {
			tem += "<tr>";
			tem += "<td><input type='radio' name='bid' value='"+s[0]+"'></td>";
			tem += "<td>"+s[1]+"</td>";
			tem += "<td>"+s[4]+"</td>";
			tem += "<td>"+s[2]+"</td>";
			tem += "<td>"+s[3]+"</td>";
			tem += "</tr>";
		}
		
		tem += "</tbody></table>";
		tem += "<button type='submit' name = 'page' value = '0'>Start Reading</button></form>";
		return tem;
	}

	private void init(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
	}
}
