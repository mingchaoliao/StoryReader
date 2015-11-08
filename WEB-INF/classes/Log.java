/*
 * Story Reader V2: Log
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to write log to file: server.log
 * */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;


public class Log {
	private static Log instance;
	
	// empty constructor
	private Log() {}
	
	//user singleton design pattern
	public static Log getInstance() {
		if(instance == null) {
			instance = new Log();
		}
		return instance;
	}
	
	//handle all log request, wirte log to server.log
	public void write(String direction, String msg, HttpServletRequest req)  {
		try {
			// user random access file 
			RandomAccessFile log = new RandomAccessFile(req.getRealPath("/")+"/server.log", "rw");
			
			// jump to file end
			log.skipBytes((int) log.length());
			
			//get date and time
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
			Date date = new Date();
			
			//write log with message
			msg = date + " " + "("+direction+" " + req.getRemoteAddr()+"("+req.getRemotePort()+") "+req.getMethod()+")\t"+ msg+"\n";
			log.write(msg.getBytes());
			log.close();
		} catch(SecurityException se) {
			System.err.println("Log File: Access denied or do not have permission to write file");
		} catch(IOException ioe) {
			System.err.println("Input/Output Error Occured");
		}
	}
}
