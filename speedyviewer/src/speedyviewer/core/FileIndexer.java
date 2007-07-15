package speedyviewer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
public class FileIndexer extends AbstractFileIndexer
{
	/**
	 * Create an indexer with the specified chunk size.
	 * 
	 * @param chunkSize
	 */
	public FileIndexer(int chunkSize)
	{
		super(chunkSize);
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
		int[] chunk = new int[getChunkSize()];

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
						chunk = new int[getChunkSize()];
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
	
}