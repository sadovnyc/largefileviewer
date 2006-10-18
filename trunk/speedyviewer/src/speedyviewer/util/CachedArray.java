package speedyviewer.util;

/**
 * Array that can grow without allocating additional chunks
 * of memory. It will not free, reallocate and copy, but maintain
 * an array of chunks, allocation new chunks when needed.
 * 
 * The operation set is quite limited, as removal of elements is
 * not supported.
 * 
 */
public class CachedArray<T>
{
	private Object[] cache;
	private int length;
	private int chunkSize;
	private int levels;
	
	/**
	 * Constructor.
	 * 
	 * @param chunkSize the size of the basic allocation unit.
	 */
	public CachedArray(int chunkSize)
	{
		this.chunkSize = chunkSize;
		cache = new Object[chunkSize];
		levels = 1;
	}

	/**
	 * Add an object at the end of the array.
	 *  
	 * @param element element to add.
	 * @return
	 */
	public boolean add(Object element)
	{
		int level;
		
		int chunkIndex;
		int offset = length;
		int levelSize = chunkSize;
		Object[] chunk = cache;
		
		for(int i = 0 ; i < levels ; i++)
			levelSize *= chunkSize;

		for(level = 1 ; level < levels ; level++)
		{
			chunkIndex = offset / levelSize;
			offset = offset % levelSize;

			if(chunk[chunkIndex] == null)
				chunk[chunkIndex] = new Object[chunkSize];
			
			chunk = (Object[])cache[chunkIndex];

			levelSize = levelSize / chunkSize;
		}
				
		chunk[offset] = element;

		length++;

		return true;
	}

	/**
	 * Clears the array, no memory is deallocated.
	 */
	public void clear()
	{
		length = 0;
	}

	/**
	 * Deallocates as much memory as possible.
	 * In Java terms: removes references, giving the
	 * memory to the GC.
	 */
	public void trim()
	{
		// TODO
	}

	public T get(int index)
	{
		if(index >= length || index < 0)
			throw new IndexOutOfBoundsException();

		int level;
		
		int chunkIndex;
		int offset = length;
		int levelSize = chunkSize;
		Object[] chunk = cache;
		
		for(int i = 0 ; i < levels ; i++)
			levelSize *= chunkSize;

		for(level = 1 ; level < levels ; level++)
		{
			chunkIndex = offset / levelSize;
			offset = offset % levelSize;

			if(chunk[chunkIndex] == null)
				chunk[chunkIndex] = new Object[chunkSize];
			
			chunk = (Object[])cache[chunkIndex];

			levelSize = levelSize / chunkSize;
		}

		return (T) chunk[offset];
	}

	public boolean isEmpty()
	{
		return length == 0;
	}


	public void set(int index, T element)
	{
		if(index > length || index < 0)
			throw new IndexOutOfBoundsException();
		else if(index == length)
			add(element);
		else
		{
			int level;
			int chunkIndex;
			int offset = index;
			int levelSize = chunkSize;
			Object[] chunk = cache;
			
			for(int i = 0 ; i < levels ; i++)
				levelSize *= chunkSize;
	
			for(level = 1 ; level < levels ; level++)
			{
				chunkIndex = offset / levelSize;
				offset = offset % levelSize;
	
				if(chunk[chunkIndex] == null)
					chunk[chunkIndex] = new Object[chunkSize];
				
				chunk = (Object[])cache[chunkIndex];
	
				levelSize = levelSize / chunkSize;
			}
		}
	}

	public int size()
	{
		return length;
	}

	public Object[] toArray()
	{
		Object[] a = new Object[length];
		
		return a;
	}

}
