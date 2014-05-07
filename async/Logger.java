package async;

import java.util.*;
import java.io.*;

public class Logger extends Thread
{
	public static boolean verbose = false;
	
	public static synchronized void log(String message)
	{
		if( verbose )
			queue.add(message);
	}
	
	public static synchronized void log(Exception e)
	{
		queue.add(e.toString());
	}
	
	private PrintStream out = null;
	private long wait = 5000L;
	private boolean shut = false;
	
	private static LinkedList<String> queue = new LinkedList<String>();
	private static Logger instance = null;
	
	public static Logger create(PrintStream out, long wait)
	{
		if( instance != null )
			throw new RuntimeException("Logger is already initialized");
		
		instance = new Logger();
		instance.out = out;
		instance.wait = wait;
		
		instance.start();
		return instance;
	}
	
	public static Logger getInstance()
	{
		return instance;
	}
	
	private Logger()
	{
	}
	
	public void run()
	{
		while( !shut || queue.size() > 0 )
		{
			try
			{
				Thread.sleep(wait);
				
				LinkedList<String> q = duplicate();
				
				for( String s : q )
					out.println(s);

				q.clear();
				out.flush();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static synchronized LinkedList<String> duplicate()
	{
		LinkedList<String> q = queue;
		queue = new LinkedList<String>();
		return q;
	}
	
	public void shutdown()
	{
		this.shut = true;
	}
}