import async.*;
import java.io.*;
import java.nio.file.*;

public class openstack
{
	private static void usage()
	{
		String usage = "\nOpenstack Swift Bruteforcer by Simon Uyttendaele\n" + 
			"Usage: java [-Xmx2g] -jar openstack.jar [OPTIONS]\n\n" +
			"Options: (* = mandatory)\n\n" +
			"\t-h,\t--help\t\tShow this help message\n" +
			"*\t-o, \t--operation\tType of operation (DELETE|UPLOAD|LIST)\n" + 
			"\t-l,\t--log-file\tLog file name (default: stdout)\n" +
			"\t-t,\t--threads\tNumber of parallel threads (default: 2)\n" +
			"\t-v,\t--verbose\t(1|0) Log more than exceptions (default: 0)\n" +
			"*\t-d,\t--directory\tThe local directory to upload or \n\t\t\t\tremote directory to delete (if not set, the \n\t\t\t\tcontainer will be dropped)\n" +
			"*\t-u,\t--username\tThe openstack username\n" +
			"*\t-p,\t--password\tThe openstack password\n" +
			"*\t-s,\t--server\tThe openstack server (https://...)\n" +
			"*\t-c,\t--container\tThe openstack container name\n";
		System.out.println(usage);
		System.exit(0);
	}
	
	public static void main(String[] args)
	{
		Logger l = null;
		
		try
		{
			String user = null;
			String password = null;
			String server = null;
			String container = null;
			String directory = null;
			PrintStream log = System.out;
			boolean verbose = false;
			int threads = 2;
			String operation = "";
			
			if( args.length % 2 != 0 )
				usage();
			
			for( int i = 0; i < args.length; i += 2 )
			{
				if( args[i].equals("-h") || args[i].equals("--help") )
					usage();
				else if( args[i].equals("-o") || args[i].equals("--operation") )
					operation = args[i+1];
				else if( args[i].equals("-l") || args[i].equals("--log-file") )
					log = new PrintStream(args[i+1]);
				else if( args[i].equals("-t") || args[i].equals("--threads") )
					threads = Integer.parseInt(args[i+1]);
				else if( args[i].equals("-d") || args[i].equals("--directory") )
					directory = args[i+1];
				else if( args[i].equals("-u") || args[i].equals("--username") )
					user = args[i+1];
				else if( args[i].equals("-p") || args[i].equals("--password") )
					password = args[i+1];
				else if( args[i].equals("-s") || args[i].equals("--server") )
					server = args[i+1];
				else if( args[i].equals("-c") || args[i].equals("--container") )
					container = args[i+1];
				else if( args[i].equals("-v") || args[i].equals("--verbose") )
					verbose = args[i+1].equals("1");
			}
			
			if( user == null || password == null || server == null || container == null || operation == null )
				usage();
			
			l = Logger.create(log, 1000);
			Logger.verbose = verbose;
			Pool p = null;
			Dispatcher d = null;
			Dispatcher.path = directory;
			
			if( operation.equalsIgnoreCase("UPLOAD") )
			{
				if( directory == null )
					usage();
				d = new LocalDispatcher();
				p = new Pool(threads, Uploader.class);
			}
			else if( operation.equalsIgnoreCase("DELETE") )
			{
				d = new RemoteDispatcher();
				p = new Pool(threads, Deleter.class);
			}
			else if( operation.equalsIgnoreCase("LIST") )
			{
				d = new RemoteDispatcher();
				p = new Pool(threads, Lister.class);
			}
			else
				usage();
			
			d.initialize(p);

			long start = System.nanoTime();
			
			Authenticator.connect(server, user, password);
			Task.url += "/" + container;

			if( operation.equalsIgnoreCase("UPLOAD") )
				Task.url += "/" + directory;

			Logger.log("Process starting...");
			
			d.start();
			p.start();
			p.join();

			long end = System.nanoTime();
			
			Logger.log("Process completed in " + ((end-start)/60000000000L) + " minutes");
		}
		catch(Exception e)
		{
			if( l != null )
				Logger.log(e);
			else
				e.printStackTrace();
		}
		finally
		{
			if( l != null )
			{
				l.shutdown();
				try { l.join(); } catch (Exception e) { }
			}
		}
	}
}