/*
 * Story Reader V2: login
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to handle user login request
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

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class Login extends HttpServlet {
	private HttpSession session;
	private HtmlHandler html;
	private FreemarkerHandler marker;
	private SQL_Handler sql;
	private PrintWriter out;
	private Log log;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//do initialization
			init(request, response);
			//process get request
			get(request,response);
		} catch(Exception e) {
			log.write("Server Error", e.getMessage(), request);
			response.sendRedirect(response.encodeRedirectURL("error.html"));
		}	
	}

	private void get(HttpServletRequest req, HttpServletResponse res) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException, SQLException {
		marker.put("menu", "<div class='list-group'>"
					+ "<a href='index.html' class='list-group-item'>Home</a>"
					+ "<a href='storyRust' class='list-group-item'>Rust API</a>"
					+ "</div>");
		marker.put("button", "");
		marker.put("list",loginForm(res,"","","",""));
		marker.process(session, out);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// do initialization
			init(request, response);

			// handle post request
			post(request,response);
		} catch(Exception e) {
			log.write("Server Error", e.getMessage(), request);
			response.sendRedirect(response.encodeRedirectURL("error.html"));
		}
	}

	private void post(HttpServletRequest req, HttpServletResponse res) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException, SQLException {
		String user = req.getParameter("user");
		String pwd = req.getParameter("pwd");
		String sec = req.getParameter("sec");

		boolean usererr=false,pwderr=false,secerr=false;
		
		if(user == null || user.equals("")) usererr = true;
		if(pwd == null || pwd.equals("")) pwderr = true;
		if(sec == null || sec.equals("")) secerr = true;
		
		if(usererr || pwderr || secerr) {
			marker.put("button", "");
			marker.put("list", loginForm(res, usererr?"<p style='color:red;display:inline;'>Please enter username</p>":""
				, pwderr?"<p style='color:red;display:inline;'>Please enter password</p>":""
					, ""
						, secerr?"<p style='color:red;display:inline;'>Please enter verification code</p>":""));
			marker.process(session, out);
		} else {
			int uid = sql.authenticate(user, pwd, sec);
			if(uid == -1) {
				marker.put("button", "");
				marker.put("list",loginForm(res,"","","<p style='color:red'>Username or password or verification code incorrect</p>",""));
				marker.process(session,out);
			} else { // user signin successfully
				log.write("Server", "Login successfully, welcome, "+user+" (uid: "+uid+")", req);
				session.setAttribute("user", user);
				session.setAttribute("uid", uid);

				//recirect user to home page
				res.sendRedirect(res.encodeRedirectURL("index.html"));
			}
		}
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
		log = Log.getInstance();
		if(session.getAttribute("user") != null) response.sendRedirect(response.encodeRedirectURL("index.html"));
	}

	// this method is used to create a login form in html format
	private String loginForm(HttpServletResponse res, String uerr, String perr, String notmatch, String secerr) {
		return "<form class='form-signin' id='login' method='post' action='"+res.encodeURL("login.html")+"' style='width:400px;'>" + "\n" +
				"<h2 class='form-signin-heading'>Please sign in</h2>" + "\n" +
				notmatch +
				"<br><label class='sr-only'>Username: "+uerr+"</label>" + "\n" +
				"<input type='text' name='user' class='form-control' placeholder='Username' required>" + "\n" +

				"<br><label class='sr-only'>Password: "+perr+"</label>" + "\n" +
				"<input type='password' name='pwd' class='form-control' placeholder='Password' required>" + "\n" +

				"<br><label class='sr-only'>Verification code: "+secerr+"</label>" + "\n" +
				"<input type='text' class='form-control' name='sec' placeholder='Two-Factor Auth Code' required>" + "\n" +

				"<br><button class='btn btn-lg btn-primary btn-block' type='submit'>Sign in</button>" + "\n" +
				"</form>";
	}

}
