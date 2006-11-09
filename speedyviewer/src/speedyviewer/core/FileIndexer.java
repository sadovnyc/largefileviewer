package speedyviewer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileIndexer
{
	protected int chunkSize;
	private int count = 0;
	private int charCount = 0;
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
		int bytesInLine = 0;
		int len;
		long start = 0;
		long computation = 0;
		long reading = 0;
	
		FileInputStream inputStream = new FileInputStream(file);
	
		start = System.currentTimeMillis();
		
		//add first line (offset 0)
		indexChunk[line++] = 0;
	
		while( (len = inputStream.read(buffer)) > 0)
		{
			reading += System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
			for(int i=0 ; i < len ; i++)
			{
				bytesInLine++;
				//check for newline: ascii code 10
				if(buffer[i] == 10 /*|| bytesInLine > 256*/) //doesn't work when using ReadLine() -> needs buffer.
				{
					indexChunk[line++] = offset + i + 1;
					bytesInLine = 0;
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
			charCount = indexChunk[len-1];
		}

		if(listener != null)
			listener.newIndexChunk(this);
	}

	public int getLineCount()
	{
		synchronized(indexCache)
		{
			return count;
		}
	}

	public int getOffsetForLine(int line)
	{
		int chunk;
		int offset;

		chunk = line / chunkSize;
		offset = line % chunkSize;

		synchronized(indexCache)
		{
			if(line >= count)
				throw new IndexOutOfBoundsException();
		
			return indexCache.get(chunk)[offset];
		}
	}

	public int getLineForOffset(int offset)
	{
		int line;
		int indexChunk;
		int[] chunk;

		synchronized(indexCache)
		{
			int min = 0;
			int max = indexCache.size();
			int chunkLast;
	
			indexChunk = min + (max - min)/2;

			//find chunk
			do
			{
				chunk = indexCache.get(indexChunk);
				chunkLast = chunk.length;
				if(indexChunk*chunkSize + chunkSize > count)
					chunkLast = count - indexChunk*chunkSize;
				
				if(offset < chunk[0])
					max = indexChunk - 1;
				else if(offset > chunk[chunkLast - 1])
					min = indexChunk;
				else
					break;

				indexChunk = min + (max - min)/2;
			}
			while(min < max );
	
			chunk = indexCache.get(indexChunk);

			//find index in chunk
			line = indexChunk * chunkSize;

			min = 0;

			if(line + chunkSize <= count)
				max = chunkSize;
			else
				max = count - line;

			int index = min + (max - min)/2;
			do
			{
				if(offset >= chunk[index] && offset < chunk[index+1])
					break;
				else if(offset < chunk[index])
					max = index - 1;
				else
					min = index + 1;
				index = min + (max - min)/2;
			}
			while(min < max);

			line += index;

			if(line >= count)
				throw new IndexOutOfBoundsException();

			return line;
		}
	}
	
	public int getCharCount()
	{
		return charCount;
	}

	public void setListener(IIndexerListener listener)
	{
		this.listener = listener;
	}

}