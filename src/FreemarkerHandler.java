/*
 * Story Reader V2: FreemarkerHandler
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to handle freemarker templete stuff
 * */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;


public class FreemarkerHandler {
	private static FreemarkerHandler instance;
	private static HttpServlet servlet;
	private static Map root;
	private static Configuration cfg;

	private FreemarkerHandler(HttpServlet servlet) throws IOException {
		this.servlet = servlet;
		this.root = new HashMap();

		// initialization, do only once
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File(servlet.getServletContext().getRealPath("/")));
		cfg.setDefaultEncoding("UTF-8");
	}
	
	// use singleton design pattern
	public static FreemarkerHandler getInstance(HttpServlet servlet) throws IOException {	
		if(instance == null) {
			instance = new FreemarkerHandler(servlet);
			instance.reset();
		}
		instance.reset();
		return instance;
	}

	public static void put(String key, String value) {
		root.put(key, value);
	}
	
	public void reset() {
		this.root = new HashMap();
	}
	
	public static void process(HttpSession session,PrintWriter out) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		process(session,out,"template.html");
	}
	
	public static void process(HttpSession session,PrintWriter out, String template) throws TemplateException, IOException {
		// handle all button stuff, if user has loging, create logout button
		// if not, create login button
		if(!root.containsKey("button")) {
			if(session.getAttribute("user") == null) {
				root.put("button","<li><a href='login.html'><span class='glyphicon glyphicon-log-in'></span> Login</a></li>");
			} else {
				root.put("button", "<li><a><span class='glyphicon'></span>Welcome, "+session.getAttribute("user")+"</a></li>"
						+"<li><a href='logout.html'><span class='glyphicon glyphicon-log-out'></span> Logout</a></li>");
			}
		}
		
		// set default value
		if(!root.containsKey("list")) root.put("list", "");
		if(!root.containsKey("menu")) root.put("menu", "");
		
		Template temp = cfg.getTemplate(template);
		temp.process(root, out);
		root = new HashMap();
	}


}
