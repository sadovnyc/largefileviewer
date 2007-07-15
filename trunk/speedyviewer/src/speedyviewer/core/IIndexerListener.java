package speedyviewer.core;

/**
 * Listener to events from the indexer, currently only
 * when a new index chunk is available.
 * 
 */
public interface IIndexerListener
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
	
	public void indexingComplete(AbstractFileIndexer indexer);
}