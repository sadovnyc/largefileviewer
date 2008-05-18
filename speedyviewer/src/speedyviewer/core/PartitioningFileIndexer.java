package speedyviewer.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class PartitioningFileIndexer extends FileIndexer
{
	private byte[] startMatch;
	private byte[] endMatch;
	
	private Stack<Integer> partitionStarts = new Stack<Integer>();
	
	private List<IndexPartition> partitions = new ArrayList<IndexPartition>();
	
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

	public List<IndexPartition> getPartitions()
	{
		return partitions;
	}

	
}
