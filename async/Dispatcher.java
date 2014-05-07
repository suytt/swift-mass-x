package async;

public abstract class Dispatcher extends Thread
{
	protected Pool pool = null;
	public void initialize(Pool pool)
	{
		this.pool = pool;
	}
	
	public static String path = null;
}