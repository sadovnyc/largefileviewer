package speedyviewer.core;

/**
 * Partion over the index.
 * 
 * @todo use line indexes or positions?
 * 
 * @author fabrizio
 *
 */
public class IndexPartition
{
	// the partition start
	private long firstIndex;
	// the partition length
	private long length;

	/**
	 * Create a partition starting at the given position
	 * and of the given length.
	 * 
	 * @param firstIndex starting position.
	 * @param length length of the selection
	 */
	public IndexPartition(long firstIndex, long length)
	{
		super();
		this.firstIndex = firstIndex;
		this.length = length;
	}

	/**
	 * Check if a partition is completely contained
	 * in this one
	 * 
	 * @param partition
	 * @return
	 */
	public boolean contains( IndexPartition partition )
	{
		return ( firstIndex <= partition.firstIndex ) && 
		( (firstIndex + length) >= (partition.firstIndex + partition.length) );
	}

	public boolean contains( long position )
	{
		return ( firstIndex <= position ) && 
		( (firstIndex + length) >= position );
	}
}
