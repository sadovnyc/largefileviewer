package speedyviewer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Exchanger;

public class DualThreadIndexer extends FileIndexer
{
	private Exchanger<DataBuffer> exchanger = new Exchanger<DataBuffer>();
	private class DataBuffer
	{
		byte[] buffer = new byte[1024*256];
		int used = 0;
	}

	/**
	 * Constructor.
	 * 
	 * @param file
	 * @param chunkSize
	 * @throws FileNotFoundException
	 */
	public DualThreadIndexer(int chunkSize) throws FileNotFoundException
	{
		super(chunkSize);
	}

	public void index(File file) throws IOException
	{
		int[] indexChunk = new int[getChunkSize()];
		int offset = 0;
		int line = 0;
		int len;
		DataBuffer buffer = new DataBuffer();
		long start = 0;
		long computation = 0;
		long reading = 0;
		
		start = System.currentTimeMillis();

		FillingLoop filler = new FillingLoop(file);
		new Thread(filler).start();

		try
		{
			while( (buffer = exchanger.exchange(buffer)) != null )
			{
				len = buffer.used;

				reading += System.currentTimeMillis() - start;
				start = System.currentTimeMillis();

				for(int i=0 ; i < len ; i++)
				{
					//check for newline: ascii code 10
					if(buffer.buffer[i] == 10)
					{
						indexChunk[line++] = offset + i + 1;
						
						//chunk filled?
						if(line == indexChunk.length)
						{
							sendIndexChunk(indexChunk, line);
							indexChunk = new int[getChunkSize()];
							line = 0;
						}
					}
				}
				//going to read next buffer, update offset
				offset += len;
				computation += System.currentTimeMillis() - start;
				start = System.currentTimeMillis();
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//last chunk (if not completely filled)
		if(line > 0)
			sendIndexChunk(indexChunk, line);

		System.out.println("computation time (ms):" + computation + " reading time (ms):" + reading);
	}

	
	private class FillingLoop implements Runnable
	{
		private File file;
		
		public FillingLoop(File file)
		{
			super();
			this.file = file;
		}

		public void run()
		{
			int len;
			DataBuffer buffer = new DataBuffer();
			FileInputStream inputStream = null;

			try
			{
				inputStream = new FileInputStream(file);

				while ((len = inputStream.read(buffer.buffer)) > 0)
				{
					buffer.used = len;
					//exchange the buffers
					buffer = exchanger.exchange(buffer);
				}
				
				exchanger.exchange(null);

				inputStream.close();

			} catch (InterruptedException ex)
			{
				// TODO
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	}
