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
		// handle all button stuff, if user has loging, create logout button
		// if not, create login button
		if(!root.containsKey("button")) {
			if(session.getAttribute("user") == null) {
				root.put("button", "<form method='get' action='login.html' style'width:30%;height:20%;margin:auto;	'><button type='submit' style='height:30%;height:40%;margin:auto;'>Sign In</button></form>");
			} else {
				root.put("button", "<h2 style='color:#00FF00;width:60%;text-size:auto;'>Welcome, "+session.getAttribute("user")+"<h2>" + "<form method='get' action='logout.html' style='width:20%;height:30%;margin:auto;'><button type='submit' style='height:30%;height:40%;margin:auto;'>Logout</button></form>");
			}
		}
		
		// set default value
		if(!root.containsKey("list")) root.put("list", "");
		if(!root.containsKey("menu")) root.put("menu", "");
		
		Template temp = cfg.getTemplate("main.ftl");
		temp.process(root, out);
		root = new HashMap();
		
	}


}
