package async;

import java.net.*;
import java.util.*;

public class Authenticator
{
	public static void connect(String url, String user, String pass) throws Exception
	{
		HttpURLConnection u = (HttpURLConnection) new URL(url).openConnection();
		u.setRequestMethod("GET");
		u.setRequestProperty("X-Auth-User", user);
		u.setRequestProperty("X-Auth-Key", pass);
		
		if( u.getResponseCode() != 200 )
			throw new Exception("Could not authenticate");

		Map<String, List<String>> h = u.getHeaderFields();
		Task.url = h.get("X-Storage-Url").get(0);
		Task.token = h.get("X-Storage-Token").get(0);
		
		Logger.log("Authentication complete");
	}
}