package async;

import java.net.*;
import java.io.*;
import java.util.*;

public class RemoteDispatcher extends Dispatcher
{
	public void run()
	{
		try
		{
			Logger.log("Listing directory " + path);
			
			list(null);
		}
		catch(Exception e)
		{
			Logger.log(e);
		}
		finally
		{
			Logger.log("Remote Dispatcher complete");
			pool.complete();
		}
	}
	
	private void list(String marker) throws Exception
	{
		long start = System.nanoTime();
		HttpURLConnection h = (HttpURLConnection) new URL(
			Task.url + 
			(path != null ? "?prefix=" + path : "") + 
			(marker != null ? "&marker=" + marker : "")
			).openConnection();
		h.setDoOutput(true);
		h.setRequestMethod("GET");
		h.setRequestProperty("X-Auth-Token", Task.token);
		h.setRequestProperty("Accept", "text/plain");
		
		int count = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(h.getInputStream()));
		for( String line = in.readLine(); line != null; line = in.readLine() )
		{
			count++;
			pool.feed(line);
			marker = line;
		}
		in.close();
		
		int response = h.getResponseCode();
		h.disconnect();
		
		long end = System.nanoTime();
		
		Logger.log("Remote Dispatcher: " + response + "\t" + count + "\t"+ ((end-start) / 1000000));
		
		if( count > 0 )
			list(marker);
	}
}