/*
 * CSE383
 * Mingchao Liao
 * Rust Server
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import freemarker.template.TemplateException;

public class Rust extends HttpServlet {
	SQL_Handler sql;
	Log log;
	HttpSession session;
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			sql = SQL_Handler.getInstance();
			log = Log.getInstance();
			session = req.getSession();
			String[] tem = req.getRequestURI().split("/");
			process(req,res,tem);
		} catch(Exception e) {
			e.printStackTrace(res.getWriter());
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			sql = SQL_Handler.getInstance();
			log = Log.getInstance();
			session = req.getSession();
			String[] tem = req.getRequestURI().split("/");
			processPost(req,res,tem);
		} catch(Exception e) {
			e.printStackTrace(res.getWriter());
		}
	}
	
	private void processPost(HttpServletRequest req, HttpServletResponse res, String[] tem) throws IOException, SQLException {
		
		if(tem.length == 5 && tem[4].equalsIgnoreCase("edit")) {
			if(session.getAttribute("user") == null && !session.getAttribute("user").equals("admin") && !sql.validateRustKey(tem[3],1)) {
				sendJSONError(req, res, "Invalid Key");
				return;
			}
			JSONObject obj = new JSONObject(req.getParameter("story"));
			String bid = obj.getString("BID");
			String title = obj.getString("Title");
			String author = obj.getString("Author");
			String publisher = obj.getString("Publisher");
			String content = obj.getString("Content");
			if(bid != null && title != null && author != null && publisher != null && content != null && !bid.equals("") && !title.equals("")) {
				boolean status = sql.editStory(bid,title,author,publisher,content);
				writeJSON(req, res, status?"edit Story BID("+bid+") Success":"edit Story BID("+bid+") Failed", 
						"status", status?"true":"false");
				System.out.println(status);
			} else {
				writeJSON(req, res, "Invalid Argument", "error", "Invalid Argument");
			}
			return;
		}
		
		if(tem.length == 5 && tem[4].equalsIgnoreCase("add")) {
			if(session.getAttribute("user") == null && !session.getAttribute("user").equals("admin") && !sql.validateRustKey(tem[3],1)) {
				sendJSONError(req, res, "Invalid Key");
				return;
			}
			JSONObject obj = new JSONObject(req.getParameter("story"));
			String title = obj.getString("Title");
			String author = obj.getString("Author");
			String publisher = obj.getString("Publisher");
			String content = obj.getString("Content");
			if(title != null && author != null && publisher != null && content != null && !title.equals("")) {
				boolean status = sql.addStory(title,author,publisher,content);
				writeJSON(req, res, status?"add Story Title("+title+") Success":"add Story Title("+title+") Failed", 
						"status", status?"true":"false");
				System.out.println(status);
			} else {
				writeJSON(req, res, "Invalid Argument", "error", "Invalid Argument");
			}
			return;
		}
		
	}

	private void process(HttpServletRequest req, HttpServletResponse res, String[] tem) throws SQLException, IOException, TemplateException {
		if(tem.length == 3 && tem[2].equalsIgnoreCase("storyRust")) {
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			FreemarkerHandler marker = FreemarkerHandler.getInstance(this);
			marker.put("menu", "<div class='list-group'>"
					+ "<a href='index.html' class='list-group-item'>Home</a>"
					+ "</div>");
			marker.put("list", "<h1>Usages: </h1><br>"
								+ "<hr>"
								+ "<h2>No Sign In Required: </h2>"
								+ "<ul>"
								+ "<li><h3>Get Key: /storyRust/getkey/{username}/{password}/{two-factor-auth-code}</h3></li>"
								+ "<li><h3>Get Story List: /storyRust/{key}/getlist</h3></li>"
								+ "<li><h3>Get Story: /storyRust/{key}/getstory/{BID}</h3></li>"
								+ "</ul>"
								+ "<h2>Sign In Required: </h2>"
								+ "<ul>"
								+ "<li><h3>Get Story List: /storyRust//getlist</h3></li>"
								+ "<li><h3>Get Story: /storyRust//getstory/{BID}</h3></li>"
								+ "</ul>");
			
			marker.process(session, out);
			return;
		}
		
		if(tem.length == 5 && tem[4].equalsIgnoreCase("getlist")) {
			if(session.getAttribute("user") == null && !sql.validateRustKey(tem[3])) {
				sendJSONError(req, res, "Invalid Key");
				return;
			}
			ArrayList<String[]> booklist = sql.getAllBooks();

			if(booklist == null) {
				sendJSONError(req, res, "ERROR");
				return;
			}
			sendBookList(req,res,booklist);

			return;
		}

		if(tem.length == 6 && tem[4].equalsIgnoreCase("getstory")) {
			if(session.getAttribute("user") == null && !sql.validateRustKey(tem[3])) {
				sendJSONError(req, res, "Invalid Key");
				return;
			}
			String bid = tem[5];
			ArrayList<String> book = sql.getBook(bid);

			if(book == null) {
				sendJSONError(req, res, "Invalid Book ID");
				return;
			}


			sendBook(req,res,book,sql.getBookInfo(bid));

			return;
		}
		
		if(tem.length == 6 && tem[4].equalsIgnoreCase("delete")) {
			if(session.getAttribute("user") == null && !session.getAttribute("user").equals("admin") && !sql.validateRustKey(tem[3],1)) {
				sendJSONError(req, res, "Invalid Key");
				return;
			}
			boolean status = sql.deleteStory(tem[5]);
			writeJSON(req, res, status?"Delete Story BID("+tem[5]+") Success":"Delete Story BID("+tem[5]+") Failed", 
					"status", status?"true":"false");
			return;
		}

		if(tem.length == 7 && tem[3].equalsIgnoreCase("getkey")) {
			int uid;
			if((uid=sql.authenticate(tem[4], tem[5], tem[6])) != -1) {
				String key = sql.generateKey(uid);
				writeJSON(req, res, "Sent key", "key",key);
				return;
			}
			sendJSONError(req, res, "Username OR Password OR SecCode Incorrect");
			return;
		}

		sendJSONError(req, res, "Invalid Arguments");

	}




	private void sendBookList(HttpServletRequest req, HttpServletResponse res,
			ArrayList<String[]> booklist) throws IOException {
		PrintWriter out = res.getWriter();

		res.setContentType("application/json");
		JSONObject json = new JSONObject();
		JSONArray arr = new JSONArray();
		for(String[] s : booklist) {
			Map map = new HashMap();
			if(s[1].contains(".sty")) {
				s[1] = s[1].substring(0, s[1].indexOf(".sty"));
			}
			map.put("BID", s[0]);
			map.put("Title", s[1]);
			map.put("Author", s[2]);
			map.put("Publisher", s[3]);
			map.put("Pages", s[4]);
			arr.put(map);
		}
		json.put("StoryList", arr);
		out.print(json.toString());

	}
	private void sendBook(HttpServletRequest req, HttpServletResponse res,
			ArrayList<String> book, String[] bookinfo) throws IOException {
		String name = bookinfo[1];
		if(name.contains(".sty")) {
			name = name.substring(0, name.indexOf(".sty"));
		}
		PrintWriter out = res.getWriter();

		res.setContentType("application/json");
		JSONObject json = new JSONObject();
		json.put("BID",bookinfo[0]);
		json.put("Title", name);
		json.put("Author", bookinfo[2]);
		json.put("Pages", bookinfo[4]);
		json.put("Publisher", bookinfo[3]);
		JSONArray content = new JSONArray();
		for(String s : book) content.put(s);
		json.put("content", content);
		out.print(json.toString());

	}
	private void sendJSONError(HttpServletRequest req, HttpServletResponse res, String msg) throws IOException {
		res.setStatus(400);
		writeJSON(req, res, "JSON ERROR - " + msg, "error",msg);
	}

	public void writeJSON(HttpServletRequest req, HttpServletResponse res, String msg, String key, String value) throws IOException {
		log.write("User", msg, req);
		PrintWriter out = res.getWriter();
		//res.setStatus(400);
		res.setContentType("application/json");
		JSONObject json = new JSONObject();
		json.put(key,value);
		out.print(json.toString());
	}
}
