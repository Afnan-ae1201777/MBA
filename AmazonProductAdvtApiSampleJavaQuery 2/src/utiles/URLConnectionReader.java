package utiles;

import java.net.*;
import java.io.*;

public class URLConnectionReader {
	private String urlString;
	private URL url;

	private URLConnection yc;

	public URLConnectionReader(String urls) throws Exception {
		super();
		this.urlString = urls;
		url = new URL(urlString);
		yc = url.openConnection();
		yc.addRequestProperty("user-agent", "Mozilla/5.0 (iPad; CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
		yc.addRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		yc.addRequestProperty("Upgrade-Insecure-Requests", "1");
		yc.connect();
	}

	public String getPageHTML() throws Exception {
		String result = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			result += inputLine;
		//	System.out.println("hi");

		}
		in.close();
		return result;
	}
public static void main(String[] args) {
	URLConnectionReader x;
	try {
		x = new URLConnectionReader("https://raw.githubusercontent.com/Afnan-ae1201777/MBA/master/mbatest.txt");

		String s= x.getPageHTML();
		System.out.println(s);
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}
}