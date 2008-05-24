package speedyviewer.core;

import java.io.File;

import org.eclipse.core.databinding.observable.list.ObservableList;

public class FileIndex
{
	private File file;
	private FileIndexer indexer;
	private ObservableList observableIndexList;

	private IIndexerMonitor monitor = new IIndexerMonitor()
	{

		@Override
		public void addingIndexChunk(AbstractFileIndexer indexer, int[] chunk,
				int len, int charCount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void indexingComplete(AbstractFileIndexer indexer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isCanceled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void newIndexChunk(AbstractFileIndexer indexer) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public FileIndex(int chunkSize, File file, ObservableList observableIndexList)
	{
		this.file = file;
		this.observableIndexList = observableIndexList;
		indexer = new FileIndexer( chunkSize );
		indexer.addListener(monitor);
	}

	
}
