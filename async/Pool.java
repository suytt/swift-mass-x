package async;

import java.nio.file.*;
import java.util.concurrent.atomic.*;

public class Pool extends Thread
{
	private AtomicInteger total = new AtomicInteger(0);
	private AtomicInteger processed = new AtomicInteger(0);
	private boolean completed = false;
	
	private Task[] instances = null;
	
	public Pool(int size, Class type) throws Exception
	{
		if( type == Task.class || !Task.class.isAssignableFrom(type) )
			throw new IllegalArgumentException("Unsupported operation");
			
		instances = new Task[size];
		for( int i = 0; i < size; i++ )
		{
			instances[i] = (Task) type.newInstance();
			instances[i].initialize(this);
		}
	}
	
	public void run()
	{
		try
		{ 
			for( int i = 0; i < instances.length; i++ )
				instances[i].start();
		
			while( !completed || total.get() != processed.get() )
				Thread.sleep(1000);
		}
		catch(Exception e)
		{
			Logger.log(e);
		}
		finally
		{
			Logger.log("Pool complete");
		}
	}
	
	public void complete()
	{
		completed = true;
		for( int i = 0; i < instances.length; i++ )
			instances[i].complete();
	}
	
	private int roundrobin = 0;
	public void feed(Object o)
	{
		int t = total.incrementAndGet();
		//Logger.log("Pool total : " + t);
		
		instances[roundrobin].add(o);
		
		roundrobin++;
		if( roundrobin >= instances.length )
			roundrobin = 0;
	}
	
	public void process()
	{
		int p = processed.incrementAndGet();
		//Logger.log("Pool processed : " + p);
	}
}