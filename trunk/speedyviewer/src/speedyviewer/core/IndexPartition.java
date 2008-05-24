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
	 * Create a partition with the given span.
	 * 
	 * @param firstIndex starting position (including).
	 * @param lastIndex ending position (including).
	 */
	public IndexPartition(long firstIndex, long lastIndex)
	{
		super();
		this.firstIndex = firstIndex;
		this.length = lastIndex - firstIndex + 1;
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
	
	public long getFirst()
	{
		return firstIndex;
	}

	public long getLast()
	{
		return firstIndex + length;
	}

	public long getLength()
	{
		return length;
	}
}
