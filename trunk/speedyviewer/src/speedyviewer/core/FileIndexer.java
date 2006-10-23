package speedyviewer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileIndexer
{
	protected int chunkSize;
	private int count = 0;
	private ArrayList<int[]> indexCache = new ArrayList<int[]>();
	private IIndexerListener listener;

	public FileIndexer(int chunkSize)
	{
		super();
		this.chunkSize = chunkSize;
	}

	public void index(File file) throws IOException
	{
		byte[] buffer = new byte[1024*256];
		int[] indexChunk = new int[chunkSize];
		int offset = 0;
		int line = 0;
		int len;
		long start = 0;
		long computation = 0;
		long reading = 0;
	
		FileInputStream inputStream = new FileInputStream(file);
	
		start = System.currentTimeMillis();
	
		while( (len = inputStream.read(buffer)) > 0)
		{
			reading += System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
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
			computation += System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
		}
		
		//last chunk (if not completely filled)
		if(line > 0)
			sendIndexChunk(indexChunk, line);
	
		System.out.println("computation time (ms):" + computation + " reading time (ms):" + reading);
		inputStream.close();
	}

	protected void sendIndexChunk(int[] indexChunk, int len)
	{
		synchronized(indexCache)
		{
			indexCache.add(indexChunk);
			count += len;
		}

		if(listener != null)
			listener.newIndexChunk(this);
	}

	public int getLineCount()
	{
		return count;
	}

	public int getOffsetForLine(int line)
	{
		int chunk;
		int offset;
	
		if(line >= count)
			throw new IndexOutOfBoundsException();
	
		chunk = line / chunkSize;
		offset = line % chunkSize;
		
		return indexCache.get(chunk)[offset];
	}
	
	public void setListener(IIndexerListener listener)
	{
		this.listener = listener;
	}

}