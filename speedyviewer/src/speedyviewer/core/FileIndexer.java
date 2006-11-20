package speedyviewer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import speedyviewer.util.ChunkIntArray;

/**
 * This class indexes the lines in a text file.
 * It stores the file offset of the first character
 * of each line, starting from line 0 -> offset 0.
 * 
 * The internal storage is a List of arrays (called chunks)
 * all of the same size. This allows the index to grow without
 * reallocating, thus performing better for huge files (many
 * millions of lines). 
 */
public class FileIndexer
{
	private int charCount = 0;
	private Vector<IIndexerListener> listeners = new Vector<IIndexerListener>();
	private ChunkIntArray index;

	/**
	 * Create an indexer with the specified chunk size.
	 * 
	 * @param chunkSize
	 */
	public FileIndexer(int chunkSize)
	{
		super();
		index = new ChunkIntArray(chunkSize);
	}

	public void index(File file) throws IOException
	{
		byte[] buffer = new byte[1024*256];
		int[] indexChunk = new int[index.getChunksize()];
		int offset = 0;
		int line = 0;
		int bytesInLine = 0;
		int len;
		long start = 0;
		long computation = 0;
		long reading = 0;
	
		index.clear();

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
						indexChunk = new int[index.getChunksize()];
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

	public void clear()
	{
		synchronized(index)
		{
			index.clear();
			charCount = 0;
		}
	}

	protected void sendIndexChunk(int[] indexChunk, int len)
	{
		synchronized(index)
		{
			index.add(indexChunk, len);
			charCount = indexChunk[len-1];
		}

		for (IIndexerListener listener : listeners)
			listener.newIndexChunk(this);
	}
	
	protected int getChunkSize()
	{
		return index.getChunksize();
	}

	public int getLineCount()
	{
		synchronized(index)
		{
			return index.size();
		}
	}

	public int getOffsetForLine(int line)
	{
		synchronized(index)
		{
			return index.get(line);
		}
	}

	public int getLineForOffset(int offset)
	{
		int line;
		int min;
		int max;
		int off1;
		int off2;

		synchronized (index)
		{
			min = 0;
			max = index.size() - 1;
			do
			{
				line = min + (max - min)/2;
				off1 = index.get(line);
				off2 = index.get(line+1);
				if(offset >= off1 && offset < off2)
					max = min = line; // found
				else if(offset >= off2)
					min = line + 1;
				else
					max = line - 1;
			}
			while(max > min);
		}
		
		line = min + (max - min)/2;
		
		return line;
	}

	public int getCharCount()
	{
		synchronized (index)
		{
			return charCount;
		}
	}

	public void addListener(IIndexerListener listener)
	{
		if(listener != null)
			listeners.add(listener);
	}

}