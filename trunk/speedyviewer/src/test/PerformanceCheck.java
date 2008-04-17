package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import speedyviewer.core.FileIndexer;

public class PerformanceCheck
{
	/**
	 * subclass indexer to test some basic additional parsing
	 *
	 */
	public static class MyIndexer extends FileIndexer
	{
		public MyIndexer(int chunkSize)
		{
			super(chunkSize);
		}

		@Override
		protected void processLine(byte[] lineBuffer, int length, int lineIndex)
		{
			if(lineBuffer[0] == 'B' && lineBuffer[1] == 'S')
			{
				System.out.println("BS line found at " + lineIndex);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("Not enough arguments (" + args.length + "):");
			for (String string : args) {
				System.out.println("    " + string);
			}
			System.out.println("Usage: PerformanceCheck <filename>");
		}
		else
		{
			Scanner scanner = null;
			File logfile = new File(args[0]);
			int lineCount = 0;

			System.out.println("Using java Scanner ");

			try
			{
				//for statistics
				long start = 0;
				long computation = 0;
			
				start = System.currentTimeMillis();

				scanner = new Scanner(logfile);
				String line;
				while(scanner.hasNextLine())
				{
					line = scanner.nextLine();
					lineCount++;
					if(line.startsWith("BS"))
						System.out.println(line);
				}
				
				computation = System.currentTimeMillis() - start;
				System.out.println("computation time (ms):" + computation );

				System.out.println("Found " + lineCount + " lines");
			}
			catch (FileNotFoundException e)
			{
				System.out.println("Failed to create scanner");
				e.printStackTrace();
			}
			finally
			{
				if( scanner != null )
					scanner.close();
			}

			System.out.println();
			System.out.println("Using FileIndexer");
			
			FileIndexer indexer = new FileIndexer(4096);
			try
			{
				indexer.index(logfile);
				lineCount = indexer.getLineCount();
				System.out.println("Found " + lineCount + " lines");
				
			} catch (IOException e)
			{
				System.out.println("Failed to create index");
				e.printStackTrace();
			}
		}
	}

}
