package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class HTTPQuery {
	String url;
	String token;
	public HTTPQuery(String url, String token) throws MalformedURLException, IOException {
		this.url = url;
		this.token = token;
	}
	public String query(String path) throws IOException {
        URL u = new URL(url+"/"+path);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Token "+token);
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		in.close();
		con.disconnect();
		return content.toString();
	}
}
