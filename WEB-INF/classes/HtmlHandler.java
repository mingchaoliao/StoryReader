/*
 * Story Reader V2: HtmlHandler
 * Mingchao Liao
 * CSE383
 * 
 * This class is use to generate some html code
 * */
public class HtmlHandler {
	private static HtmlHandler instance = null;

	private HtmlHandler() {}

	// use singleton design pattern
	public static HtmlHandler getInstance() {
		if(instance == null) {
			instance = new HtmlHandler();
		}
		return instance;
	}
	
	//create a normal tag
	public static String add(String tag, String value) {
		return "<"+tag+">"+value+"</"+tag+">";
	}
	
	// create tag with style
	public static String add(String tag, String style, String value) {
		return "<"+tag+" style='"+style+"'>"+value+"</"+tag+">";
	}
}
