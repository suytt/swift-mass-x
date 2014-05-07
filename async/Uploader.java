package async;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Uploader extends Task
{
	private LinkedList<Path> queue = new LinkedList<Path>();
	
	public synchronized void add(Object o)
	{
		if( !(o instanceof Path) )
			throw new IllegalArgumentException("Input is not a valid Path");
			
		queue.push((Path) o);
	}
	
	private synchronized Path next()
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
				upload(next());
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
	
	private void upload(Path p) throws Exception
	{
		long start = System.nanoTime();
		byte[] data = Files.readAllBytes(p);
		
		Path path2 = Paths.get(Dispatcher.path);
		String file = p.toString().substring(path2.toString().length()).replaceAll("\\\\", "/");
		HttpURLConnection h = (HttpURLConnection) new URL(url + file).openConnection();
		h.setDoOutput(true);
		h.setRequestMethod("PUT");
		h.setRequestProperty("Content-Type", "application/octet-stream");
		h.setRequestProperty("X-Auth-Token", token);
		h.setRequestProperty("Content-Length", "" + data.length);
		
		OutputStream out = h.getOutputStream();
		out.write(data);
		out.close();
		
		int response = h.getResponseCode();
		h.disconnect();
		
		long end = System.nanoTime();
		
		BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
		Logger.log("Uploader: " + response + "\t" + file + "\t" + attrs.size() + "\t"+ ((end-start) / 1000000));
	}
}