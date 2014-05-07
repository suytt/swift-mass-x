package async;

import java.io.*;
import java.net.*;
import java.util.*;

public class Deleter extends Task
{
	private LinkedList<String> queue = new LinkedList<String>();
	
	public synchronized void add(Object o)
	{
		if( !(o instanceof String) )
			throw new IllegalArgumentException("Input is not a valid String");
			
		queue.push((String) o);
	}
	
	private synchronized String next()
	{
		return queue.pop();
	}
	
	private synchronized boolean hasNext()
	{
		return queue.size() > 0;
	}
	
	public void run()
	{
		while( !completed || hasNext() )
		{
			try
			{
				if( !hasNext() )
				{
					Thread.sleep(10);
					continue;
				}
			}
			catch(Exception e)
			{
				Logger.log(e);
			}
			
			try
			{
				delete(next());
			}
			catch(Exception e)
			{
				Logger.log(e);
			}
			finally
			{
				pool.process();
			}
		}
	}
	
	private void delete(String file) throws Exception
	{
		long start = System.nanoTime();
		HttpURLConnection h = (HttpURLConnection) new URL(url + "/" + file).openConnection();
		h.setDoOutput(true);
		h.setRequestMethod("DELETE");
		h.setRequestProperty("X-Auth-Token", token);
		
		int response = h.getResponseCode();
		h.disconnect();
		
		long end = System.nanoTime();
		
		Logger.log("Deleter: " + response + "\t" + file + "\t"+ ((end-start) / 1000000));
	}
}