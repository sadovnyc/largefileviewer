package speedyviewer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;


/**
 * File Indexer that creates partitions of the
 * document using the given sequences.
 * 
 * A partition starts whenever a line starts with the given
 * startMatch sequence and ends at first successive line
 * that starts with the given endMatch sequence.
 * 
 * Therefore partitions may be nested but can not overlap.
 * 
 * @author iannetti
 *
 */
public class PartitioningFileIndexer extends FileIndexer
{
	private byte[] startMatch;
	private byte[] endMatch;

	//stack used to remember the partitions start points
	private Stack<Integer> partitionStarts = new Stack<Integer>();
	
	//the list of partitions, updated during indexing
	private List<IndexPartition> partitions = new ArrayList<IndexPartition>();
	
	/**
	 * Creates an indexer that computes partitions based on the given 
	 * start/endMatch sequences.
	 * 
	 * @param chunkSize the index chunk (see {@link FileIndexer}).
	 * @param startMatch the sequence identifying a partition start.
	 * @param endMatch the sequence identifying a partition end.
	 */
	public PartitioningFileIndexer(int chunkSize, byte[] startMatch, byte[] endMatch)
	{
		super(chunkSize);
		this.startMatch = startMatch;
		this.endMatch = endMatch;
	}

	public PartitioningFileIndexer(int chunkSize, String startMatch, String endMatch)
	{
		super(chunkSize);
		this.startMatch = startMatch.getBytes();
		this.endMatch = endMatch.getBytes();
	}

	@Override
	protected void processLine(byte[] lineBuffer, int length, int lineIndex)
	{
		int i;
		int len = Math.min(lineBuffer.length, startMatch.length);
		boolean match = true;
		if( len > 0 )
		{
			for( i = 0 ; i < len ; i++ )
			{
				if( startMatch[i] != lineBuffer[i] )
				{
					match = false;
					break;
				}
			}
			if( match )
			{
				partitionStarts.push(lineIndex);
			}
		}

		len = Math.min(lineBuffer.length, endMatch.length);
		match = true;

		if( len > 0 )
		{
			for( i = 0 ; i < len ; i++ )
			{
				if( endMatch[i] != lineBuffer[i] )
				{
					match = false;
					break;
				}
			}
			if( match )
			{
				int startIndex;
				if(partitionStarts.size() > 0)
					startIndex = partitionStarts.pop();
				else
					startIndex = 0;

				partitions.add( new IndexPartition( startIndex, lineIndex ) );
			}
		}
	}

	/**
	 * Get the partitions found during indexing.
	 * 
	 * @return the partition list.
	 */
	public List<IndexPartition> getPartitions()
	{
		int startIndex;
		while( partitionStarts.size() > 0 )
		{
			startIndex = partitionStarts.pop();
			partitions.add( new IndexPartition( startIndex, getLineCount() - 1 ) );
		}
		
		Collections.sort(partitions, new Comparator<IndexPartition>() {
			@Override
			public int compare(IndexPartition arg0, IndexPartition arg1)
			{
				long diff = arg0.getFirst() - arg1.getFirst();

				if( diff < 0)
					return -1;
				else if( diff == 0 )
					return 0;
				else
					return 1;
			}
		});
		return partitions;
	}

	
}
