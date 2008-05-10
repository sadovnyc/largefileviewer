package speedyviewer.util;

import java.util.ArrayList;

/**
 * Array of long that can grow without performing re-allocation.
 * The data is stored in chunks of a given size, every time new space
 * is needed a new chunk is allocated.
 * This result in a slight performance loss when accessing elements
 * with the benefit of reduced memory needs.
 * 
 * This class is NOT synchronised.
 */
public class ChunkLongArray
{
	protected int chunkSize;
	private int count = 0;
	private ArrayList<long[]> indexCache = new ArrayList<long[]>();

	/**
	 * Create an array with the specified chunk size.
	 * 
	 * @param chunkSize
	 */
	public ChunkLongArray(int chunkSize)
	{
		super();
		this.chunkSize = chunkSize;
	}


	/**
	 * Clears the array releasing all chunks. 
	 */
	public void clear()
	{
		indexCache.clear();
		count = 0;
	}

	/**
	 * @return the number of elements.
	 */
	public int size()
	{
		return count;
	}

	/**
	 * Get the chunk size used by this array.
	 * 
	 * @return the chunk size.
	 */
	public int getChunksize()
	{
		return chunkSize;
	}

	/**
	 * Get element at position index.
	 * 
	 * @param index the index of the element to retrieve.
	 * @return the element.
	 */
	public long get(int index)
	{
		int chunk;
		int offset;

		chunk = index / chunkSize;
		offset = index % chunkSize;

		if(index >= count)
			throw new IndexOutOfBoundsException();
	
		return indexCache.get(chunk)[offset];
	}

	/**
	 * Add an element to the array, allocate a new chunk
	 * if necessary.
	 * 
	 * @param element element to add.
	 */
	public void add(long element)
	{
		int chunk;
		int offset;

		chunk = count / chunkSize;
		offset = count % chunkSize;
		
		if(offset == 0)
		{
			//need a new chunk
			indexCache.add(new long[chunkSize]);
		}

		indexCache.get(chunk)[offset] = element;
		count++;
	}

	/**
	 * Add an array of elements.
	 * If the array has exactly the size of a chunk and
	 * the current count of elements is a multiple of the
	 * chunk size, the specified array is used as new chunk
	 * (regardless of the len parameter).
	 *  
	 * @param elements elements to add
	 * @param len how many elements of the array should be added.
	 */
	public void add(long[] elements, int len)
	{
		int offset;

		offset = count % chunkSize;

		if(len > elements.length)
			len = elements.length;

		if(offset == 0 && elements.length == chunkSize)
		{
			//optimisation: add the chunk directly
			indexCache.add(elements);
			count += len;
		}
		else
		{
			for (int i = 0 ; i < len ; i++)
				add(elements[i]);
		}
	}
}
