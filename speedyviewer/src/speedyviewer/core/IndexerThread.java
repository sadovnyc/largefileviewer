package speedyviewer.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author  fiannetti
 */
public class IndexerThread extends Thread
{
	private File file;
	private LargeFileIndexer indexer;
	
	public IndexerThread(int chunkSize, File file) throws FileNotFoundException
	{
		super("indexing:" + file.getPath());
		this.file = file;
		this.indexer = new LargeFileIndexer(chunkSize);
	}

	public void run()
	{
		try {
			indexer.index(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized int getLineCount()
	{
		return indexer.chunkSize;
	}
	
	public synchronized int[] getLineDelimiters(int index)
	{
		return null;
	}
}
