package speedyviewer.core;

public interface IPartitioningListener
{
	/**
	 * Called whenever a new partition has been found to the list.
	 * 
	 * @param indexer
	 * @param partition
	 */
	void newPartition(PartitioningFileIndexer indexer, IndexPartition partition);
}
