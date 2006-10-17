package speedyviewer.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CachedArray<T> implements List<T>
{
	private Object[] cache;
	private int length;
	private int chunkSize;
	private int levels;
	
	public CachedArray(int chunkSize)
	{
		this.chunkSize = chunkSize;
		cache = new Object[chunkSize];
		levels = 1;
	}

	public boolean add(Object arg0)
	{
		int level;
		
		int chunkIndex;
		int offset = length;
		Object[] chunk = cache;
		
		for(level = 1 ; level < levels ; level++)
		{
			chunkIndex = offset / chunkSize;
			offset = offset % chunkSize;
			if(chunk[chunkIndex] == null)
				chunk[chunkIndex] = new Object[chunkSize];
			
			chunk = (Object[])cache[chunkIndex];
		}		
		
		
		chunk[offset] = arg0;

		length++;

		return true;
	}

	public void add(int index, Object arg1)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection arg0)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int arg0, Collection arg1)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		length = 0;
	}

	public boolean contains(Object arg0)
	{
		return false;
	}

	public boolean containsAll(Collection arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public T get(int index)
	{
		if(index >= length || index < 0)
			throw new IndexOutOfBoundsException();

		int chunkIndex = index / chunkSize;
		int offset = index % chunkSize;
		
		Object[] chunk = (Object[])cache[chunkIndex];

		return (T) chunk[offset];
	}

	public int indexOf(Object arg0)
	{
		return 0;
	}

	public boolean isEmpty()
	{
		return length == 0;
	}

	public Iterator<T> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int lastIndexOf(Object arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public ListIterator<T> listIterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ListIterator<T> listIterator(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Object arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public T remove(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removeAll(Collection arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public T set(int arg0, Object arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int size()
	{
		return length;
	}

	public List<T> subList(int arg0, int arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] toArray()
	{
		Object[] a = new Object[length];
		
		return a;
	}

	public Object[] toArray(Object[] arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
