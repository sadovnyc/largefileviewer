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

	/**
	 * Index the given file.
	 * This operation is synchronous.
	 * 
	 * @param file file to index.
	 * @throws IOException in case open/read/close operations fail.
	 */
	public void index(File file) throws IOException
	{
		int[] chunk = new int[index.getChunksize()];

		//read buffer
		byte[] buffer = new byte[1024*256];
		int bufferOffset = 0;
		int len;

		//overall line count
		int line = 0;
		
		int bytesInLine = 0;
		
		//for statistics
		long start = 0;
		long computation = 0;
		long reading = 0;
	
		//initialise
		clear();
		FileInputStream inputStream = new FileInputStream(file);
	
		start = System.currentTimeMillis();
		
		//add first line (offset 0)
		chunk[line++] = 0;
	
		while( (len = inputStream.read(buffer)) > 0)
		{
			reading += System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
			//now scan the read buffer
			for(int i=0 ; i < len ; i++)
			{
				bytesInLine++;
				//check for newline: ascii code 10
				if(buffer[i] == 10 /*|| bytesInLine > 256*/) //doesn't work when using ReadLine() -> needs buffer.
				{
					//if the chunk is already full 'send' it and create a new one
					if(line == chunk.length)
					{
						sendIndexChunk(chunk, line, bufferOffset + i + 1 - chunk[0]);
						chunk = new int[index.getChunksize()];
						line = 0;
					}
					//next line starts from the character after new line 
					chunk[line++] = bufferOffset + i + 1;
					bytesInLine = 0;
				}
			}
			//going to read next buffer, update offset
			bufferOffset += len;
			computation += System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
		}

		//last chunk (if not completely filled)
		if(line > 0)
			sendIndexChunk(chunk, line, bufferOffset - chunk[0]);
	
		//create a temporary array for notifications
		IIndexerListener[] listenerArray = listeners.toArray(new IIndexerListener[listeners.size()]);
		for (IIndexerListener listener : listenerArray)
			listener.indexingComplete(this);

		System.out.println("computation time (ms):" + computation + " reading time (ms):" + reading);
		inputStream.close();
	}

	/**
	 * Clears the index.
	 * 
	 */
	public void clear()
	{
		synchronized(index)
		{
			index.clear();
			charCount = 0;
		}
	}

	protected void sendIndexChunk(int[] indexChunk, int len, int chunkCharCount)
	{
		//create a temporary array for notifications
		IIndexerListener[] listenerArray = listeners.toArray(new IIndexerListener[listeners.size()]);

		for (IIndexerListener listener : listenerArray)
			listener.addingIndexChunk(this, indexChunk, len, chunkCharCount);
		
		synchronized(index)
		{
			index.add(indexChunk, len);
			charCount += chunkCharCount;
		}

		for (IIndexerListener listener : listenerArray)
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
			if(line < index.size())
				return index.get(line);
			else if(line == index.size())
				return charCount;
			else
				throw new ArrayIndexOutOfBoundsException();
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
			//allow offset at end of file
			if( charCount > 0 && offset >= charCount)
				return index.size() - 1; //size is always > 0

			min = 0;
			max = index.size() - 1; //size is always > 0
			
			//treat max == 0 as special case, otherwise
			//line+1 below would be > max
			//max < 0 means index.size() == 0
			if(max <= 0)
				return 0;

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