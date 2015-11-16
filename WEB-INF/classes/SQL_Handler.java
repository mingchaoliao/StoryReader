/*
 * Story Reader V2: SQL_Handler
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to handler all mysql stuff
 * */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;


/*
 * Database Schema
 * 
 * book
 * +---------+----------------+------+-----+---------+----------------+
 * | Field   | Type           | Null | Key | Default | Extra          |
 * +---------+----------------+------+-----+---------+----------------+
 * | bid     | int(11)        | NO   | PRI | NULL    | auto_increment |
 * | name    | varbinary(100) | NO   |     | NULL    |                |
 * | content | longtext       | NO   |     | NULL    |                |
 * +---------+----------------+------+-----+---------+----------------+
 * 
 * user
 * +-----------+----------------+------+-----+---------+----------------+
 * | Field     | Type           | Null | Key | Default | Extra          |
 * +-----------+----------------+------+-----+---------+----------------+
 * | uid       | int(11)        | NO   | PRI | NULL    | auto_increment |
 * | username  | varbinary(100) | NO   |     | NULL    |                |
 * | password  | varbinary(100) | NO   |     | NULL    |                |
 * | secretkey | varbinary(50)  | YES  |     | NULL    |                |
 * +-----------+----------------+------+-----+---------+----------------+
 * 
 */

public class SQL_Handler {
	private static final String DB_URL = "jdbc:mysql://172.17.29.114/story";
	private static final String DB_USER = "383-sql";
	private static final String DB_PWD = "test123";

	private static SQL_Handler instance = null;
	private static Connection conn = null;
	private static MessageDigest md;

	private SQL_Handler() {
		try {
			// connect to database
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PWD);

			// do md5 encrypt for password
			md = MessageDigest.getInstance("MD5");
		} catch(Exception e) {
			System.err.println("sql error");
		}
	}

	//use singleton dessign pattern
	public static SQL_Handler getInstance() {
		instance = null;
		if(instance == null) {
			instance = new SQL_Handler();
		}
		return instance;
	}

	// handle sql query: select
	public static ResultSet executeQuery(String sql) throws SQLException {
		return conn.createStatement().executeQuery(sql);
	}
	
	// handle sql query: insert, update
	public static int executeUpdate(String sql) throws SQLException {
		return conn.createStatement().executeUpdate(sql);
	}

	// authenticate user and password
	// if match, return user id
	// if not, return -1
	public static int authenticate(String username, String password, String sec) throws SQLException, UnsupportedEncodingException {
		int verify = 0;
		try {
			verify = Integer.parseInt(sec);
		} catch(Exception e) {
			return -1;
		}
		
		password = computeHash(password);
		ResultSet rs = executeQuery("select * from user where username='"+username+"' and password='"+password+"'");
		if(rs.next()) {
			int id = Integer.parseInt(rs.getString("uid"));
			String secretkey = rs.getString("secretkey");
			return (new GoogleAuthenticator().authorize(secretkey, verify)) ? id : -1;
		} else {
			return -1;
		}
	}

	//do a registration (ONLY for TEST now)
	public static boolean register(String username, String password) throws SQLException, UnsupportedEncodingException {
		//get googleAuth secret key
		String k = new GoogleAuthenticator().createCredentials().getKey();
		password = computeHash(password);
		String sql = "insert into user(username,password, secretkey) values('"+username+"', '"+password+"','"+k+"');";
		int rs = executeUpdate(sql);
		return rs > 0;
	}

	// insert book to database (ONLY for TEST now)
	public static void insertBooks(File directory) throws SQLException, FileNotFoundException {
		File[] list = directory.listFiles();
		for(File file : list) {
			if(file.isFile()) {
				Scanner sc = new Scanner(file);
				String content = "";
				while(sc.hasNextLine()) content += (sc.nextLine() + "\n");
				executeUpdate("insert into book(name,content) values('"+file.getName()+"', '"+content+"');");
			}
		}
	}
	
	// get a list of all book name
	// if no book exist, return null
	public static ArrayList<String[]> getAllBooks() throws SQLException {
		ResultSet rs = executeQuery("select bid,name from book");
		ArrayList<String[]> list = new ArrayList<String[]>();
		while(rs.next()) {
			list.add(new String[]{rs.getString("bid"),rs.getString("name")});
		}
		return (list==null ? null : list);
	}
	
	// get all pages form specified book
	public static ArrayList<String> getBook(String bid) throws SQLException {
		ResultSet rs = executeQuery("select content from book where bid="+bid+";");
		ArrayList<String> pages = new ArrayList<String>();
		if(!rs.next()) return null;
		String sss = rs.getString("content");
		Scanner sc = new Scanner(sss);
		String cap="";
		
		// cat pages using tang <PAGE></PAGE>
		while(sc.hasNextLine()) {
			String s = sc.nextLine();
			if(s.equals("<PAGE>")) {
				cap = "";
			} else if(s.equals("</PAGE>")) {
				pages.add(cap);
			} else {
				cap += s;
			}
		}
		return pages;
	}

	// do md5 encrypt for password
	private static String computeHash(String password) throws UnsupportedEncodingException {
		byte[] array = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
       }
        return sb.toString();
	}

	public static String generateKey(int uid) throws SQLException {
		Random rnd = new Random(System.currentTimeMillis());
		String key = "";
		for(int i = 0; i < 15; i++) {
			key += ((char) (rnd.nextInt(122-97+1)+97));
		}
		key += uid;
		//System.out.println(key);
		executeUpdate("update user set rustkey = '"+key+"' where uid = "+uid+";");
		return key;
	}
	
	public static boolean validateRustKey(String key) throws SQLException {
		ResultSet re = executeQuery("select * from user where rustkey = '"+key+"';");
		return re.next();
	}
	
	public static String getBookName(String bid) throws SQLException {
		ResultSet re = executeQuery("select name from book where bid = "+bid+";");
		if(re.next()) {
			return re.getString("name");
		}
		return null;
	}

}
