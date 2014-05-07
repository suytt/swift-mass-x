package async;

import java.util.*;

public abstract class Task extends Thread
{
	public static String url = null;
	public static String token = null;
	
	public abstract void add(Object o);
	
	protected boolean completed = false;
	public void complete()
	{
		this.completed = true;
	}
	
	protected Pool pool = null;
	public void initialize(Pool pool)
	{
		this.pool = pool;
	}
	
}