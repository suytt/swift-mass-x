package async;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.*;

public class LocalDispatcher extends Dispatcher
{
	public void run()
	{
		try
		{
			Path path2 = Paths.get(path);
			
			Logger.log("Scanning directory " + path2);
			Files.walkFileTree(path2, new SimpleFileVisitor<Path>()
			{
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					pool.feed(file);
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch(Exception e)
		{
			Logger.log(e);
		}
		finally
		{
			Logger.log("Local Dispatcher complete");
			pool.complete();
		}
	}
}