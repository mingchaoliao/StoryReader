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

import org.json.JSONArray;
import org.json.JSONObject;

public class Rust extends HttpServlet {
	SQL_Handler sql;
	Log log;
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			sql = SQL_Handler.getInstance();
			log = Log.getInstance();
			String[] tem = req.getRequestURI().split("/");
			process(req,res,tem);
		} catch(Exception e) {
			e.printStackTrace(res.getWriter());
		}
	}
	private void process(HttpServletRequest req, HttpServletResponse res, String[] tem) throws SQLException, IOException {
		if(tem.length == 5 && tem[4].equalsIgnoreCase("getlist")) {
			if(!sql.validateRustKey(tem[3])) {
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
			if(!sql.validateRustKey(tem[3])) sendJSONError(req, res, "Invalid Key");
			String bid = tem[5];
			ArrayList<String> book = sql.getBook(bid);
			
			if(book == null) {
				sendJSONError(req, res, "Invalid Book ID");
				return;
			}
			
			String name = sql.getBookName(bid);
			sendBook(req,res,book, bid, name);
			
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
			map.put("bid", s[0]);
			map.put("title", s[1]);
			arr.put(map);
		}
		json.put("StoryList", arr);
		out.print(json.toString());
		
	}
	private void sendBook(HttpServletRequest req, HttpServletResponse res,
			ArrayList<String> book, String bid, String name) throws IOException {
		if(name.contains(".sty")) {
			name = name.substring(0, name.indexOf(".sty"));
		}
		PrintWriter out = res.getWriter();
		
		res.setContentType("application/json");
		JSONObject json = new JSONObject();
		json.put("BID",bid);
		json.put("Name", name);
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
