/*
 * Story Reader V2: Read
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to get book from database to let user read
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

public class Read extends HttpServlet {
	private HttpSession session;
	private HtmlHandler html;
	private FreemarkerHandler marker;
	private SQL_Handler sql;
	private PrintWriter out;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			// do initialization
			init(req,res);
			//handle http get request
			get(req,res);
		} catch (Exception e) {
			Log.getInstance().write("Server Error", e.getMessage(), req);
			res.sendRedirect(res.encodeRedirectURL("error.html"));
		}
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			// do initialization
			init(req,res);
			//handle http post request
			post(req,res);
		} catch (Exception e) {
			Log.getInstance().write("Server Error", e.getMessage(), req);
			res.sendRedirect(res.encodeRedirectURL("error.html"));
		}
	}




	private void post(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException, TemplateException {
		// check if user has not sign in
		if(session.getAttribute("user") == null) {
			res.sendRedirect(res.encodeRedirectURL("error.html"));
			return;
		}
		
		String bid = req.getParameter("bid");
		
		//check if user did not select a book
		if(bid == null) {
			session.setAttribute("error", "error");
			res.sendRedirect(res.encodeRedirectURL("select.html"));
			return;
		}
		
		// check if user select a wrong book
		if(bid.equals("")) {
			res.sendRedirect(res.encodeRedirectURL("error.html"));
			return;
		}
		
		//get all pages of a specified book
		ArrayList<String> pages = sql.getBook(bid);
		
		// if reqult is null, book is not exist
		if(pages == null) {
			marker.put("list","<h1>This book does not exist!<h1><br><a href='index.html'><h1>Click me to home page<h1></a>");
			marker.process(session, out);
			return;
		}
		
		// log user behaviour 
		Log.getInstance().write("User", "User("+session.getAttribute("user")+") start reading book(id: "+bid+")", req);
		
		// do freemarker templete stuff
		marker.put("menu", generateMenu(bid, pages, 0, res));
		marker.put("list",pages.get(0));
		marker.process(session, out);
		
	}
	
	// this method is used to generate menu bar
	private String generateMenu(String bid, ArrayList<String> pages, int i, HttpServletResponse res) {
		String rtn = "";
		rtn += "<li><a href='"+res.encodeURL("index.html")+"'>Home</a></li>";
		
		// if has next page, generate a link
		if(i > 0) {
			rtn += "<li><a href='"+res.encodeURL("read.html"+"?page="+(i-1))+"'>Prev</a></li>";
			
		}
		
		// if has previous link, generate a link
		if(i < pages.size()-1) {
			rtn += "<li><a href='"+res.encodeURL("read.html"+"?page="+(i+1))+"'>Next</a></li>";
		}
		
		session.setAttribute("bid", bid);
		return rtn;
	}
	
	
	private void get(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException, TemplateException {
		
		// check if user has not signin
		// check if book does not exist
		if(session.getAttribute("user") == null || req.getParameter("page") == null) {
			res.sendRedirect(res.encodeRedirectURL("error.html"));
			return;
		}
		
		// get page number
		int page = Integer.parseInt(req.getParameter("page"));
		
		// get book id
		String bid = (String) session.getAttribute("bid");
		
		// check if book id is invalid
		if(bid == null || bid.equals("")) {
			res.sendRedirect(res.encodeRedirectURL("error.html"));
			return;
		}
		
		// get all pages of a book
		ArrayList<String> pages = sql.getBook(bid);
		
		if(pages == null) {
			marker.put("list","<h1>This book does not exist!<h1><br><a href='index.html'><h1>Click me to home page<h1></a>");
			marker.process(session,out);
			return;
		}
		
		if(page < 0 || page >= pages.size()) res.sendRedirect(res.encodeRedirectURL("error.html"));
		
		
		marker.put("menu", generateMenu(bid, pages, page, res));
		marker.put("list",pages.get(page));
		marker.process(session,out);
		
	}

	// do initialization styff
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
