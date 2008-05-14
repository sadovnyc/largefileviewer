package speedyviewer.core;

import java.io.File;
import java.io.IOException;

/**
 * Thread that uses a FileIndexer to build a
 * map of line numbers to offests for a given file.
 * 
 * Calling start() on this class will start the indexing,
 * progress is reported via the indexer listener.
 */
public class IndexerThread extends Thread
{
	//the file to index
	private File file;

	//the indexer
	private AbstractFileIndexer indexer;
	
	/**
	 * Creates a thread to index the given file.
	 * 
	 * @param chunkSize the size of index chunks.
	 * @param file the file to index.
	 */
	public IndexerThread(AbstractFileIndexer indexer, File file)
	{
		super("indexing:" + file.getPath());
		this.file = file;
		this.indexer = indexer;
	}

	/**
	 * Implementation of run.
	 *  
	 * @see java.lang.Thread#run()
	 */
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
		return indexer.getLineCount();
	}
	
	/**
	 * Get the offsets of beginning (first character)
	 * and end (line separator) of a line.
	 * Thus, the line spans from values[0] to
	 * values[1] in the file.
	 * 
	 * @param line the line number 
	 * @param values result array (must have length >= 2)
	 */
	public synchronized void getLineDelimiters(int line, int[] values)
	{
		values[0] = indexer.getOffsetForLine(line);
		values[1] = indexer.getOffsetForLine(line+1) - 1;
	}
	
	/**
	 * Set the index listener.
	 * The listener is called from the indexer thread.
	 * 
	 * @param listener
	 */
	public void setListener(IIndexerMonitor listener)
	{
		indexer.addListener(listener);
	}
	
	public synchronized int getLineForOffset(int offset)
	{
		return indexer.getLineForOffset(offset);
	}
	
	public synchronized int getCharacterCount()
	{
		return indexer.getCharCount();
	}
	
	public synchronized AbstractFileIndexer getIndexer()
	{
		return indexer;
	}
}
