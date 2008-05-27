package speedyviewer.core;

import java.io.File;

import org.eclipse.core.databinding.observable.AbstractObservable;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.ObservableList;

public class FileIndex extends AbstractObservable
{
	//the file to index
	private File file;
	//the indexer used to index
	private FileIndexer indexer;
	//the observable list of partitions given by the client
	private ObservableList observablePartitionList;
	//the thread used to index
	private IndexerThread thread;
	
	private class IndexMonitor implements IIndexerMonitor, IPartitioningListener
	{
		private boolean cancel = false;

		@Override
		public void addingIndexChunk(AbstractFileIndexer indexer, int[] chunk, int len, int charCount) { }

		@Override
		public void indexingComplete(AbstractFileIndexer indexer)
		{
			fireEvent(new ChangeEvent(FileIndex.this));
		}

		@Override
		public boolean isCanceled()
		{
			return cancel;
		}

		@Override
		public void newIndexChunk(AbstractFileIndexer indexer) { }

		@Override
		public void newPartition(PartitioningFileIndexer indexer, IndexPartition partition)
		{
			// add partition to the observable
			observablePartitionList.add( partition );
		}

		/**
		 * Cancel the monitored indexing at the next opportunity.
		 */
		public void cancel()
		{
			cancel = true;
		}
	}

	private IndexMonitor monitor = new IndexMonitor();
	
	/**
	 * Create a file index on the given file.
	 * 
	 * 
	 * @param chunkSize size of index chunks (see {@link FileIndexer}.)
	 * @param file the file to index
	 * @param observablePartitionList filled with partitions
	 */
	public FileIndex(int chunkSize, File file, ObservableList observablePartitionList)
	{
		super(Realm.getDefault());
		this.file = file;
		this.observablePartitionList = observablePartitionList;
		indexer = new FileIndexer( chunkSize );
		indexer.addListener(monitor);
	}

	/**
	 * Start file indexing in a separate thread.
	 */
	public void startIndexing()
	{
		thread = new IndexerThread( indexer, file );
		thread.start();
	}

	public void cancelIndexing( boolean waitFor )
	{
		monitor.cancel();
		if( waitFor )
		{
			try
			{
				thread.join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the current line count.
	 * 
	 * This method is thread safe.
	 * 
	 * @return the line count.
	 */
	public int getLineCount()
	{
		return indexer.getLineCount();
	}

	/**
	 * Get the current character count.
	 * 
	 * This method is thread safe.
	 * 
	 * @return the character count.
	 */
	public int getCharCount()
	{
		return indexer.getCharCount();
	}

	/**
	 * Get the line containing the specified offset.
	 * 
	 * This method is thread safe.
	 * 
	 * @param offset the offset to look for.
	 * @return the corresponding line index.
	 */
	public int getLineForOffset(int offset)
	{
		return indexer.getLineForOffset(offset);
	}
	
	/**
	 * Get the offset of the first char for the given line.
	 * 
	 * This method is thread safe.
	 * 
	 * @param line the line to look for.
	 * @return the char offset.
	 */
	public int getOffsetForLine(int line)
	{
		return indexer.getOffsetForLine(line);
	}

	/**
	 * Add an index monitor.
	 * 
	 * The monitor is called from the indexer thread.
	 * 
	 * @param monitor
	 */
	public void addListener(IIndexerMonitor monitor)
	{
		indexer.addListener(monitor);
	}

	@Override
	public boolean isStale()
	{
		return false;
	}
}
