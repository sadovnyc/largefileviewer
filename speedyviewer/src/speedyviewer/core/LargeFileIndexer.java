package speedyviewer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class LargeFileIndexer
{
	int chunkSize;
	private ArrayList<int[]> indexCache = new ArrayList<int[]>();
	
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
						sendIndexChunk(indexChunk);
						indexChunk = new int[chunkSize];
						line = 0;
					}
				}
			}
			//going to read next buffer, update offset
			offset += len;
		}
		
		inputStream.close();
	}
	
	private void sendIndexChunk(int[] indexChunk)
	{
		indexCache.add(indexChunk);
	}
}
