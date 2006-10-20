package speedyviewer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;
import java.util.concurrent.SynchronousQueue;

public class LargeFileIndexer
{
	int chunkSize;
	int count = 0;

	private Exchanger<DataBuffer> exchanger = new Exchanger<DataBuffer>();
	private ArrayList<int[]> indexCache = new ArrayList<int[]>();
	
	class DataBuffer
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
	public LargeFileIndexer(int chunkSize) throws FileNotFoundException
	{
		this.chunkSize = chunkSize;
	}

	public void index(File file) throws IOException
	{
		byte[] buffer = new byte[1024*256];
		int[] indexChunk = new int[chunkSize];
		int offset = 0;
		int line = 0;
		int len;
	
		FileInputStream inputStream = new FileInputStream(file);
	
		while( (len = inputStream.read(buffer)) > 0)
		{
			for(int i=0 ; i < len ; i++)
			{
				//check for newline: ascii code 10
				if(buffer[i] == 10)
				{
					indexChunk[line++] = offset + i + 1;
					
					//chunk filled?
					if(line == indexChunk.length)
					{
						sendIndexChunk(indexChunk, line);
						indexChunk = new int[chunkSize];
						line = 0;
					}
				}
			}
			//going to read next buffer, update offset
			offset += len;
		}
		
		//last chunk (if not completely filled)
		if(line > 0)
			sendIndexChunk(indexChunk, line);
	
		inputStream.close();
	}
	
	private void sendIndexChunk(int[] indexChunk, int len)
	{
		indexCache.add(indexChunk);
		count += len;
	}
	
	public int getLineCount()
	{
		return count;
	}

	public int getOffsetForLine(int line)
	{
		int chunk;
		int offset;
		
		chunk = line / chunkSize;
		offset = line % chunkSize;
		
		return indexCache.get(chunk)[offset];
	}


	public void parallelIndex(File file) throws IOException
	{
		int[] indexChunk = new int[chunkSize];
		int offset = 0;
		int line = 0;
		int len;
		DataBuffer buffer = new DataBuffer();
		
		FillingLoop filler = new FillingLoop(file);
		new Thread(filler).start();

		try
		{
			while( (buffer = exchanger.exchange(buffer)) != null )
			{
				len = buffer.used;

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
							indexChunk = new int[chunkSize];
							line = 0;
						}
					}
				}
				//going to read next buffer, update offset
				offset += len;
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//last chunk (if not completely filled)
		if(line > 0)
			sendIndexChunk(indexChunk, line);
	
	}

	class FillingLoop implements Runnable
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
