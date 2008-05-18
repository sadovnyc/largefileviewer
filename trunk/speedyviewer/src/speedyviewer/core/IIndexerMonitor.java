package speedyviewer.core;

/**
 * Listener to events from the indexer, currently only
 * when a new index chunk is available.
 * 
 */
public interface IIndexerMonitor
{

	/**
	 * A new chunk of index offsets is going to be added.
	 * 
	 * @param indexer the source indexer.
	 * @param chunk the chunk of offsets that is going to be added.
	 * @param len the number of valid entries in the chunk.
	 * @param charCount number of characters parsed for this chunk.
	 */
	public void addingIndexChunk(AbstractFileIndexer indexer, int[] chunk, int len, int charCount);
	
	/**
	 * A new chunk of index offsets is available in the indexer.
	 * 
	 * @param indexer the indexer
	 */
	public void newIndexChunk(AbstractFileIndexer indexer);
	
	/**
	 * The indexing is complete.
	 * 
	 * @param indexer the indexer
	 */
	public void indexingComplete(AbstractFileIndexer indexer);

	/**
	 * Used by the indexer to check if the indexing should stop.
	 * 
	 * @return true if the indexing should stop.
	 */
	public boolean isCanceled();
}
