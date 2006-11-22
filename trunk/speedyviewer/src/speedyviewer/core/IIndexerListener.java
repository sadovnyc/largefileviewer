package speedyviewer.core;

/**
 * Listener to events from the indexer, currently only
 * when a new index chunk is available.
 * 
 */
public interface IIndexerListener
{
	/**
	 * A new chunk of line offsets is available in the indexer.
	 * 
	 * @param indexer the indexer
	 */
	public void newIndexChunk(FileIndexer indexer);
	
	public void addingIndexChunk(FileIndexer indexer, int[] chunk, int len, int charCount);
	
	public void indexingComplete(FileIndexer indexer);
}
